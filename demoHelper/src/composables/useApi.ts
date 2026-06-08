import type { GenerateRequest, GenerateResponse } from '../types'

export interface StreamCallbacks {
  onProgress: (step: number) => void
}

/**
 * Real API composable — calls the Spring Boot backend.
 */
export function useApi() {
  async function generate(request: GenerateRequest): Promise<GenerateResponse> {
    const response = await fetch('/api/generate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    })

    if (!response.ok) {
      const body = await response.json().catch(() => ({}))
      const detail =
        (body as { detail?: string }).detail ||
        `请求失败 (HTTP ${response.status})`
      throw new Error(detail)
    }

    return response.json() as Promise<GenerateResponse>
  }

  /**
   * Streaming variant: reads SSE events for real-time progress.
   * Returns the final GenerateResponse once the stream completes.
   */
  async function generateStream(
    request: GenerateRequest,
    callbacks: StreamCallbacks,
  ): Promise<GenerateResponse> {
    const response = await fetch('/api/generate/stream', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    })

    if (!response.ok) {
      const body = await response.json().catch(() => ({}))
      throw new Error(
        (body as { detail?: string }).detail ||
        `请求失败 (HTTP ${response.status})`,
      )
    }

    const reader = response.body!.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    return new Promise((resolve, reject) => {
      function pump() {
        reader.read().then(({ done, value }) => {
          if (done) {
            reject(new Error('SSE stream ended without result'))
            return
          }

          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          // Keep the last partial line in the buffer
          buffer = lines.pop() || ''

          let currentEvent = ''
          for (const line of lines) {
            if (line.startsWith('event:')) {
              currentEvent = line.slice(6).trim()
            } else if (line.startsWith('data:')) {
              const data = line.slice(5).trim()

              if (currentEvent === 'progress') {
                callbacks.onProgress(Number(data))
              } else if (currentEvent === 'result') {
                try {
                  resolve(JSON.parse(data) as GenerateResponse)
                  return
                } catch {
                  reject(new Error('Failed to parse result JSON'))
                  return
                }
              } else if (currentEvent === 'error') {
                try {
                  const err = JSON.parse(data) as { message?: string }
                  reject(new Error(err.message || '服务器内部错误'))
                } catch {
                  reject(new Error('服务器内部错误'))
                }
                return
              }
              currentEvent = ''
            }
          }

          pump()
        }).catch(reject)
      }

      pump()
    })
  }

  return { generate, generateStream }
}
