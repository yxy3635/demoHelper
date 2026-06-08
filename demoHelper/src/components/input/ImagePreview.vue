<script setup lang="ts">
interface Props {
  imageBase64: string
  imageName: string | null
}

defineProps<Props>()

const emit = defineEmits<{
  remove: []
}>()
</script>

<template>
  <div class="image-preview">
    <img :src="imageBase64" :alt="imageName || 'uploaded image'" class="image-preview__img" />
    <div class="image-preview__info">
      <span class="image-preview__name">{{ imageName || 'image' }}</span>
      <button
        class="image-preview__remove"
        @click="emit('remove')"
        aria-label="remove image"
        title="remove image"
      >
        <svg
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.image-preview {
  position: relative;
  border: 1px solid var(--accent-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--code-bg);
  max-width: 300px;
}

.image-preview__img {
  display: block;
  width: 100%;
  max-height: 160px;
  object-fit: contain;
}

.image-preview__info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  background: var(--bg);
  border-top: 1px solid var(--border);
}

.image-preview__name {
  font-size: 12px;
  color: var(--text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-preview__remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--text);
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.image-preview__remove:hover {
  background: var(--color-error-bg);
  color: var(--color-error);
}
</style>
