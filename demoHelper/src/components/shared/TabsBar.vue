<script setup lang="ts">
import type { TabKey } from '../../types'

interface Props {
  modelValue: TabKey
  tabs: { key: TabKey; label: string; icon?: string }[]
}

defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [key: TabKey]
}>()

function handleKeydown(event: KeyboardEvent, currentIndex: number, tabs: Props['tabs']) {
  let nextIndex: number | null = null
  if (event.key === 'ArrowRight') {
    nextIndex = (currentIndex + 1) % tabs.length
  } else if (event.key === 'ArrowLeft') {
    nextIndex = (currentIndex - 1 + tabs.length) % tabs.length
  }
  if (nextIndex !== null) {
    event.preventDefault()
    emit('update:modelValue', tabs[nextIndex].key)
  }
}
</script>

<template>
  <div class="tabs-bar" role="tablist" :aria-label="'视图切换'">
    <button
      v-for="(tab, idx) in tabs"
      :key="tab.key"
      role="tab"
      :aria-selected="modelValue === tab.key"
      :tabindex="modelValue === tab.key ? 0 : -1"
      class="tabs-bar__tab"
      :class="{ 'tabs-bar__tab--active': modelValue === tab.key }"
      @click="emit('update:modelValue', tab.key)"
      @keydown="handleKeydown($event, idx, tabs)"
    >
      <span v-if="tab.icon" class="tabs-bar__icon">{{ tab.icon }}</span>
      {{ tab.label }}
    </button>
  </div>
</template>

<style scoped>
.tabs-bar {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  background: var(--code-bg);
  border-radius: var(--radius-md);
}

.tabs-bar__tab {
  padding: 8px 18px;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--text);
  font-size: var(--font-sm);
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: 6px;
}

.tabs-bar__tab:hover {
  color: var(--text-h);
}

.tabs-bar__tab--active {
  background: var(--bg);
  color: var(--text-h);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.tabs-bar__icon {
  font-size: 15px;
}
</style>
