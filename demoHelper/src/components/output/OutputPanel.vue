<script setup lang="ts">
import { ref, computed } from 'vue'
import type { GenerateResponse, TabKey } from '../../types'
import { formatHours, formatDifficulty, formatDate } from '../../utils/formatters'
import TabsBar from '../shared/TabsBar.vue'
import ExportBar from './ExportBar.vue'
import MarkdownPreview from './MarkdownPreview.vue'
import JsonView from './JsonView.vue'
import PlanSectionCard from './PlanSectionCard.vue'
import { useExport } from '../../composables/useExport'

interface Props { result: GenerateResponse }
const props = defineProps<Props>()
const activeTab = ref<TabKey>('cards')

const tabs: { key: TabKey; label: string }[] = [
  { key: 'cards', label: '卡片视图' },
  { key: 'markdown', label: 'Markdown' },
  { key: 'json', label: 'JSON' },
]

const { getMarkdown, getFullPrompt, copyToClipboard } = useExport()
const markdownContent = computed(() => getMarkdown(props.result))
const copyAllStatus = ref<'idle' | 'copied' | 'error'>('idle')

async function handleCopyAll() {
  copyAllStatus.value = await copyToClipboard(getFullPrompt(props.result))
  if (copyAllStatus.value !== 'idle') setTimeout(() => { copyAllStatus.value = 'idle' }, 2000)
}
</script>

<template>
  <div class="output-panel">
    <!-- Header -->
    <div class="output-panel__header">
      <div class="output-panel__top-row">
        <h2 class="output-panel__title">{{ result.title }}</h2>
        <button class="copy-all-btn" @click="handleCopyAll">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
          </svg>
          {{ copyAllStatus === 'copied' ? '已复制全部提示词' : '复制全部提示词' }}
        </button>
      </div>
      <p class="output-panel__desc">{{ result.description }}</p>
      <div class="output-panel__meta">
        <span class="meta-badge">{{ result.metadata.totalSections }} 个章节</span>
        <span class="meta-badge">预估 {{ formatHours(result.metadata.estimatedTotalHours) }}</span>
        <span class="meta-badge meta-badge--difficulty" :class="`meta-badge--${result.metadata.difficulty}`">
          {{ formatDifficulty(result.metadata.difficulty) }}
        </span>
        <span class="meta-badge meta-badge--time">{{ formatDate(result.metadata.generatedAt) }}</span>
      </div>
    </div>

    <!-- Toolbar -->
    <div class="output-panel__toolbar">
      <TabsBar v-model="activeTab" :tabs="tabs" />
      <ExportBar :result="result" />
    </div>

    <!-- Content -->
    <div class="output-panel__content">
      <Transition name="fade" mode="out-in">
        <!-- Cards View -->
        <div v-if="activeTab === 'cards'" key="cards" class="sections-list">
          <PlanSectionCard
            v-for="(section, idx) in result.sections"
            :key="section.id"
            :section="section"
            :index="idx"
          />
        </div>

        <!-- Markdown View -->
        <MarkdownPreview
          v-else-if="activeTab === 'markdown'"
          key="markdown"
          :markdown="markdownContent"
        />

        <!-- JSON View -->
        <JsonView v-else key="json" :result="result" />
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.output-panel {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.output-panel__header {
  padding: var(--space-xl);
  border-bottom: 1px solid var(--border);
  background: linear-gradient(135deg, var(--accent-bg) 0%, var(--bg) 40%);
}

.output-panel__top-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-md);
  margin-bottom: var(--space-sm);
}

.output-panel__title {
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--text-h);
  margin: 0;
}

.copy-all-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--accent);
  color: #fff;
  font-size: var(--font-sm);
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.copy-all-btn:hover {
  opacity: 0.9;
  box-shadow: 0 4px 16px var(--accent-border);
}

.output-panel__desc {
  font-size: var(--font-base);
  color: var(--text);
  line-height: 1.6;
  margin: 0 0 var(--space-md);
}

.output-panel__meta {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  flex-wrap: wrap;
}
.meta-badge {
  font-size: var(--font-xs); font-weight: 600; padding: 4px 12px;
  border-radius: var(--radius-full); background: var(--bg-secondary); color: var(--text);
}
.meta-badge--difficulty { text-transform: uppercase; }
.meta-badge--beginner { background: var(--color-success-bg); color: var(--color-success); }
.meta-badge--intermediate { background: var(--color-warning-bg); color: var(--color-warning); }
.meta-badge--advanced { background: var(--color-error-bg); color: var(--color-error); }
.meta-badge--time { opacity: 0.5; font-weight: 500; }

.output-panel__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-md) var(--space-xl);
  border-bottom: 1px solid var(--border);
  flex-wrap: wrap;
  gap: var(--space-sm);
}

.output-panel__content {
  padding: var(--space-xl);
}

.sections-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

@media (max-width: 768px) {
  .output-panel__top-row { flex-direction: column; }
  .copy-all-btn { width: 100%; justify-content: center; }
  .output-panel__content { padding: var(--space-md); }
  .output-panel__toolbar { flex-direction: column; align-items: stretch; }
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
