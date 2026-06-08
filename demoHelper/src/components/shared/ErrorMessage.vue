<script setup lang="ts">
interface Props { message: string }
defineProps<Props>()
const emit = defineEmits<{ retry: [] }>()
</script>

<template>
  <div class="error-banner" role="alert" aria-live="assertive">
    <div class="error-banner__content">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
      </svg>
      <span>{{ message }}</span>
    </div>
    <button class="error-banner__retry" @click="emit('retry')">重试</button>
  </div>
</template>

<style scoped>
.error-banner { display: flex; align-items: center; justify-content: space-between; gap: var(--space-md); padding: var(--space-md) var(--space-lg); background: var(--color-error-bg); border: 1px solid color-mix(in srgb, var(--color-error) 30%, transparent); border-radius: var(--radius-md); color: var(--color-error); font-size: var(--font-sm); }
.error-banner__content { display: flex; align-items: center; gap: var(--space-sm); }
.error-banner__retry { padding: 6px 16px; border: 1px solid var(--color-error); border-radius: var(--radius-sm); background: transparent; color: var(--color-error); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); white-space: nowrap; }
.error-banner__retry:hover { background: var(--color-error); color: #fff; }
</style>
