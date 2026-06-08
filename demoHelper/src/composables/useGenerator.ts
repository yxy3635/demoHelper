import { ref } from 'vue'
import type { GenerateResponse } from '../types'
import { validateGenerateInput } from '../utils/validators'
import { useApi } from './useApi'
import { useSettings } from './useSettings'
import type { AppState } from '../types'

const { generateStream } = useApi()

// Shared singleton state
const result = ref<GenerateResponse | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)
const appState = ref<AppState>('idle')

// Progress: real-time step index from backend SSE events
const progressStep = ref(-1)

export function useGenerator() {
  const { settings } = useSettings()

  async function generate(
    intentText: string,
    imageBase64: string | null,
  ): Promise<void> {
    const validation = validateGenerateInput(intentText, imageBase64)
    if (!validation.valid) {
      error.value = validation.error
      appState.value = 'error'
      return
    }

    isLoading.value = true
    error.value = null
    result.value = null
    progressStep.value = -1
    appState.value = 'loading'

    try {
      result.value = await generateStream(
        {
          intentText: intentText || undefined,
          imageBase64: imageBase64 || undefined,
          apiKey: settings.value.apiKey || undefined,
          baseUrl: settings.value.baseUrl || undefined,
          model: settings.value.model || undefined,
          provider: settings.value.provider || undefined,
        },
        {
          onProgress: (step: number) => {
            progressStep.value = step
          },
        },
      )
      appState.value = 'success'
    } catch (e) {
      error.value = e instanceof Error ? e.message : '生成失败，请重试'
      appState.value = 'error'
    } finally {
      isLoading.value = false
    }
  }

  function reset() {
    result.value = null
    error.value = null
    progressStep.value = -1
    appState.value = 'idle'
  }

  return {
    result,
    isLoading,
    error,
    progressStep,
    appState,
    generate,
    reset,
  }
}
