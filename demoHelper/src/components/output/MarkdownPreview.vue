<script setup lang="ts">
import { computed, onMounted, watch, ref } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark-dimmed.css'

interface Props {
  markdown: string
}

const props = defineProps<Props>()
const previewRef = ref<HTMLDivElement>()

const renderedHtml = computed(() => {
  const raw = marked.parse(props.markdown, { async: false }) as string
  return DOMPurify.sanitize(raw, {
    ADD_ATTR: ['target', 'rel'],
  })
})

function highlightCodeBlocks() {
  if (previewRef.value) {
    previewRef.value.querySelectorAll('pre code').forEach((block) => {
      hljs.highlightElement(block as HTMLElement)
    })
  }
}

onMounted(highlightCodeBlocks)
watch(renderedHtml, () => {
  // Wait for DOM update then highlight
  setTimeout(highlightCodeBlocks, 0)
})
</script>

<template>
  <div
    ref="previewRef"
    class="markdown-preview"
    v-html="renderedHtml"
  />
</template>

<style scoped>
.markdown-preview {
  padding: var(--space-lg) var(--space-xl);
  line-height: 1.7;
  color: var(--text);
}

/* Markdown element overrides */
.markdown-preview :deep(h1) {
  font-size: var(--font-2xl);
  margin: 0 0 var(--space-lg);
  padding-bottom: var(--space-md);
  border-bottom: 2px solid var(--border);
}

.markdown-preview :deep(h2) {
  font-size: var(--font-xl);
  margin: var(--space-xl) 0 var(--space-md);
}

.markdown-preview :deep(h3) {
  font-size: var(--font-lg);
  margin: var(--space-lg) 0 var(--space-sm);
}

.markdown-preview :deep(h4) {
  font-size: var(--font-base);
  margin: var(--space-md) 0 var(--space-sm);
  color: var(--accent);
}

.markdown-preview :deep(h5) {
  font-size: var(--font-sm);
  margin: var(--space-md) 0 var(--space-sm);
}

.markdown-preview :deep(p) {
  margin: 0 0 var(--space-md);
}

.markdown-preview :deep(blockquote) {
  margin: 0 0 var(--space-md);
  padding: var(--space-sm) var(--space-lg);
  border-left: 3px solid var(--accent);
  background: var(--accent-bg);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
  color: var(--text-h);
}

.markdown-preview :deep(blockquote p) {
  margin: var(--space-sm) 0;
}

.markdown-preview :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 0 0 var(--space-md);
  font-size: var(--font-sm);
}

.markdown-preview :deep(th),
.markdown-preview :deep(td) {
  padding: 8px 12px;
  border: 1px solid var(--border);
  text-align: left;
}

.markdown-preview :deep(th) {
  background: var(--code-bg);
  font-weight: 600;
}

.markdown-preview :deep(pre) {
  margin: 0 0 var(--space-md);
  padding: var(--space-md);
  background: #1e1e2e;
  border-radius: var(--radius-md);
  overflow-x: auto;
}

.markdown-preview :deep(pre code) {
  font-family: var(--mono);
  font-size: 13px;
  line-height: 1.55;
  background: transparent;
  padding: 0;
}

.markdown-preview :deep(code) {
  font-family: var(--mono);
  font-size: 13px;
  padding: 2px 6px;
  background: var(--code-bg);
  border-radius: 3px;
}

.markdown-preview :deep(hr) {
  border: none;
  border-top: 1px solid var(--border);
  margin: var(--space-lg) 0;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  padding-left: var(--space-xl);
  margin: 0 0 var(--space-md);
}

.markdown-preview :deep(li) {
  margin-bottom: 4px;
}

.markdown-preview :deep(a) {
  color: var(--accent);
  text-decoration: none;
}

.markdown-preview :deep(a:hover) {
  text-decoration: underline;
}

.markdown-preview :deep(strong) {
  font-weight: 600;
  color: var(--text-h);
}
</style>
