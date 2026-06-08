import { ref, watch } from 'vue'

export interface ApiSettings {
  provider: 'deepseek' | 'openai' | 'custom'
  apiKey: string
  baseUrl: string
  model: string
}

const STORAGE_KEY = 'demohelper-settings'

const DEFAULTS: Record<string, ApiSettings> = {
  deepseek: {
    provider: 'deepseek',
    apiKey: '',
    baseUrl: 'https://api.deepseek.com',
    model: 'deepseek-v4-flash',
  },
  openai: {
    provider: 'openai',
    apiKey: '',
    baseUrl: 'https://api.openai.com',
    model: 'gpt-4o',
  },
  custom: {
    provider: 'custom',
    apiKey: '',
    baseUrl: 'https://api.deepseek.com',
    model: 'deepseek-v4-flash',
  },
}

function load(): ApiSettings {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      if (parsed.provider && parsed.apiKey !== undefined) {
        return { ...DEFAULTS.deepseek, ...parsed }
      }
    }
  } catch { /* corrupted data, fall through */ }
  return { ...DEFAULTS.deepseek }
}

const settings = ref<ApiSettings>(load())

watch(settings, (val) => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(val))
}, { deep: true })

export function useSettings() {
  function updateSettings(patch: Partial<ApiSettings>) {
    settings.value = { ...settings.value, ...patch }

    // Auto-fill defaults when switching provider
    if (patch.provider && patch.provider in DEFAULTS) {
      const def = DEFAULTS[patch.provider]
      if (!patch.baseUrl) settings.value.baseUrl = def.baseUrl
      if (!patch.model) settings.value.model = def.model
    }
  }

  function isConfigured(): boolean {
    return settings.value.apiKey.trim().length > 0
  }

  return { settings, updateSettings, isConfigured }
}
