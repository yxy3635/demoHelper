<script setup lang="ts">
import { computed } from 'vue'
import type { GenerateResponse } from '../../types'
import CopyButton from '../shared/CopyButton.vue'

interface Props { result: GenerateResponse }
const props = defineProps<Props>()
const jsonText = computed(() => JSON.stringify(props.result, null, 2))
</script>

<template>
  <div class="json-view">
    <div class="json-view__toolbar">
      <CopyButton :text="jsonText" label="复制 JSON" />
    </div>
    <pre class="json-view__code"><code>{{ jsonText }}</code></pre>
  </div>
</template>

<style scoped>
.json-view { border: 1px solid var(--border); border-radius: var(--radius-md); overflow: hidden; }
.json-view__toolbar { display: flex; justify-content: flex-end; padding: 8px 12px; background: var(--code-bg); border-bottom: 1px solid var(--border); }
.json-view__code { margin: 0; padding: var(--space-lg); background: #1e1e2e; color: #cdd6f4; font-family: var(--mono); font-size: 13px; line-height: 1.6; overflow-x: auto; max-height: 600px; overflow-y: auto; }
</style>
