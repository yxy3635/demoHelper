import { ref, watchEffect } from 'vue'

const STORAGE_KEY = 'vibecoding-theme'

type Theme = 'light' | 'dark'

function getInitialTheme(): Theme {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored === 'light' || stored === 'dark') return stored
  } catch { /* ignore */ }
  return 'light'
}

const theme = ref<Theme>(getInitialTheme())

function applyTheme(t: Theme) {
  if (typeof document !== 'undefined') {
    document.documentElement.setAttribute('data-theme', t)
  }
}

watchEffect(() => applyTheme(theme.value))

export function useTheme() {
  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    try { localStorage.setItem(STORAGE_KEY, theme.value) } catch { /* ignore */ }
  }

  return { theme, toggleTheme }
}
