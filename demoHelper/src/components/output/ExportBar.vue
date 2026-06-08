<script setup lang="ts">
import { ref } from 'vue'
import type { GenerateResponse } from '../../types'
import { useExport } from '../../composables/useExport'

interface Props { result: GenerateResponse }
const props = defineProps<Props>()
const { copyToClipboard, downloadMarkdown, downloadJson, getMarkdown, getFullPrompt } = useExport()

const copyAllStatus = ref<'idle' | 'copied' | 'error'>('idle')
const copyMdStatus = ref<'idle' | 'copied' | 'error'>('idle')

async function handleCopyAll() {
  copyAllStatus.value = await copyToClipboard(getFullPrompt(props.result))
  if (copyAllStatus.value !== 'idle') setTimeout(() => { copyAllStatus.value = 'idle' }, 2000)
}

async function handleCopyMd() {
  copyMdStatus.value = await copyToClipboard(getMarkdown(props.result))
  if (copyMdStatus.value !== 'idle') setTimeout(() => { copyMdStatus.value = 'idle' }, 2000)
}
</script>

<template>
  <div class="export-bar">
    <button class="export-bar__btn export-bar__btn--primary" @click="handleCopyAll">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
      </svg>
      {{ copyAllStatus === 'copied' ? '已复制' : '复制全部提示词' }}
    </button>
    <button class="export-bar__btn" @click="handleCopyMd">
      {{ copyMdStatus === 'copied' ? '已复制' : '复制 Markdown' }}
    </button>
    <button class="export-bar__btn" @click="downloadMarkdown(result)">下载 .md</button>
    <button class="export-bar__btn" @click="downloadJson(result)">下载 .json</button>
  </div>
</template>

<style scoped>
.export-bar { display: flex; gap: var(--space-sm); flex-wrap: wrap; }
.export-bar__btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 6px 14px; border: 1px solid var(--border); border-radius: var(--radius-sm);
  background: var(--bg); color: var(--text); font-size: 13px; cursor: pointer;
  transition: all var(--transition-fast);
}
.export-bar__btn:hover { border-color: var(--accent-border); color: var(--accent); }
.export-bar__btn--primary {
  background: var(--accent-bg); border-color: var(--accent-border); color: var(--accent);
  font-weight: 600;
}
.export-bar__btn--primary:hover { background: var(--accent); color: #fff; }
</style>
