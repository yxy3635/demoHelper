import { ref } from 'vue'
import type { GenerateResponse } from '../types'
import { validateGenerateInput } from '../utils/validators'
import { useApi } from './useApi'
import type { AppState } from '../types'

const { generate: apiGenerate } = useApi()

// Shared singleton state so different components stay in sync
const result = ref<GenerateResponse | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)
const appState = ref<AppState>('idle')

export function useGenerator() {
  async function generate(intentText: string, imageBase64: string | null) {
    const validation = validateGenerateInput(intentText, imageBase64)
    if (!validation.valid) {
      error.value = validation.error
      appState.value = 'error'
      return
    }

    isLoading.value = true
    error.value = null
    result.value = null
    appState.value = 'loading'

    try {
      result.value = await apiGenerate({
        intentText: intentText || undefined,
        imageBase64: imageBase64 || undefined,
      })
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
    appState.value = 'idle'
  }

  return {
    result,
    isLoading,
    error,
    appState,
    generate,
    reset,
  }
}
