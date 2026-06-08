<script setup lang="ts">
import { ref } from 'vue'
import type { PlanSection } from '../../types'
import { formatHours, formatDifficulty, formatDifficultyColor } from '../../utils/formatters'
import CopyButton from '../shared/CopyButton.vue'

interface Props { section: PlanSection; index: number }
defineProps<Props>()
const expanded = ref(false)
</script>

<template>
  <div class="section-card" :class="{ 'section-card--expanded': expanded }">
    <button class="section-card__header" @click="expanded = !expanded">
      <div class="section-card__left">
        <div class="section-card__order" :style="{ background: `color-mix(in srgb, var(--accent) ${100 - index * 8}%, var(--accent-2))` }">
          {{ index + 1 }}
        </div>
        <div class="section-card__info">
          <span class="section-card__title">{{ section.title }}</span>
          <div class="section-card__meta">
            <span class="meta-tag">{{ formatHours(section.estimatedHours) }}</span>
            <span class="meta-tag" :style="{ color: formatDifficultyColor(section.difficulty), background: `color-mix(in srgb, ${formatDifficultyColor(section.difficulty)} 10%, transparent)` }">
              {{ formatDifficulty(section.difficulty) }}
            </span>
          </div>
        </div>
      </div>
      <svg class="chevron" :class="{ 'chevron--open': expanded }" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10" opacity="0.15"/><polyline points="8 10 12 14 16 10"/>
      </svg>
    </button>

    <Transition name="expand">
      <div v-if="expanded" class="section-card__body">
        <p class="section-card__desc">{{ section.description }}</p>
        <div class="prompt-box">
          <div class="prompt-box__header">
            <div class="prompt-box__label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
              提示词
            </div>
            <CopyButton :text="section.prompt" label="复制此提示词" />
          </div>
          <pre class="prompt-box__content">{{ section.prompt }}</pre>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.section-card {
  background: var(--bg); border: 1px solid var(--border); border-radius: var(--radius-lg);
  overflow: hidden; transition: all var(--transition-base); box-shadow: var(--shadow-sm);
}
.section-card--expanded { border-color: var(--accent-border); box-shadow: var(--shadow-md); }

.section-card__header {
  display: flex; align-items: center; justify-content: space-between;
  width: 100%; padding: var(--space-lg); border: none; background: transparent;
  cursor: pointer; text-align: left; transition: background var(--transition-fast);
}
.section-card__header:hover { background: var(--bg-secondary); }

.section-card__left { display: flex; align-items: center; gap: var(--space-md); min-width: 0; }
.section-card__order {
  display: flex; align-items: center; justify-content: center;
  width: 38px; height: 38px; border-radius: var(--radius-md);
  color: #fff; font-size: 16px; font-weight: 800; flex-shrink: 0;
  box-shadow: 0 2px 8px var(--accent-border);
}
.section-card__info { display: flex; flex-direction: column; gap: 6px; min-width: 0; }
.section-card__title { font-size: var(--font-base); font-weight: 650; color: var(--text-h); }
.section-card__meta { display: flex; align-items: center; gap: 8px; }
.meta-tag {
  font-size: 12px; font-weight: 600; padding: 2px 10px; border-radius: var(--radius-full);
  background: var(--bg-secondary); color: var(--text);
}

.chevron { color: var(--text); opacity: 0.4; flex-shrink: 0; transition: all var(--transition-fast); }
.chevron--open { opacity: 0.8; transform: rotate(180deg); }

.section-card__body { padding: 0 var(--space-lg) var(--space-lg); }
.section-card__desc {
  font-size: var(--font-sm); color: var(--text); line-height: 1.6;
  margin: 0 0 var(--space-lg); padding-top: var(--space-md); border-top: 1px solid var(--border);
}

.prompt-box { border: 1px solid var(--border); border-radius: var(--radius-md); overflow: hidden; }
.prompt-box__header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px var(--space-md);
  background: linear-gradient(135deg, var(--code-bg), var(--bg-secondary));
  border-bottom: 1px solid var(--border);
}
.prompt-box__label {
  display: flex; align-items: center; gap: 6px;
  font-size: 12px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.6px; color: var(--accent);
}
.prompt-box__content {
  margin: 0; padding: var(--space-md);
  background: #1a1b2e; color: #cdd6f4;
  font-family: var(--mono); font-size: 13px; line-height: 1.65;
  white-space: pre-wrap; word-break: break-word;
  max-height: 360px; overflow-y: auto;
}

.expand-enter-active { transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1); overflow: hidden; }
.expand-leave-active { transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); overflow: hidden; }
.expand-enter-from { opacity: 0; max-height: 0; }
.expand-leave-to { opacity: 0; max-height: 0; }
</style>
