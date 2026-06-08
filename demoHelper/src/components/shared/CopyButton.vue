<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  text: string
  label?: string
}

const props = withDefaults(defineProps<Props>(), { label: '复制' })

const status = ref<'idle' | 'copied' | 'error'>('idle')

async function handleCopy() {
  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(props.text)
    } else {
      const textarea = document.createElement('textarea')
      textarea.value = props.text
      textarea.style.cssText = 'position:fixed;opacity:0'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
    }
    status.value = 'copied'
    setTimeout(() => { status.value = 'idle' }, 2000)
  } catch {
    status.value = 'error'
    setTimeout(() => { status.value = 'idle' }, 2000)
  }
}
</script>

<template>
  <button class="copy-btn" :class="`copy-btn--${status}`" :aria-label="status === 'copied' ? '已复制' : label" @click="handleCopy">
    <svg v-if="status === 'idle'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
    </svg>
    <svg v-else-if="status === 'copied'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <polyline points="20 6 9 17 4 12"/>
    </svg>
    <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
    </svg>
    <span class="copy-btn__text">{{ status === 'copied' ? '已复制' : status === 'error' ? '失败' : label }}</span>
  </button>
</template>

<style scoped>
.copy-btn { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; border: 1px solid var(--border); border-radius: var(--radius-sm); background: var(--bg); color: var(--text); font-size: var(--font-sm); cursor: pointer; transition: all var(--transition-fast); }
.copy-btn:hover { border-color: var(--accent-border); color: var(--accent); }
.copy-btn--copied { border-color: var(--color-success); color: var(--color-success); background: var(--color-success-bg); }
.copy-btn--error { border-color: var(--color-error); color: var(--color-error); background: var(--color-error-bg); }
.copy-btn__text { font-size: 13px; }
</style>
