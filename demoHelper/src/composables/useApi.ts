import type { GenerateRequest, GenerateResponse } from '../types'

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

  return { generate }
}
