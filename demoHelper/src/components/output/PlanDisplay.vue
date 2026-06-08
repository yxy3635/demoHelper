<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import type { GenerateResponse } from '../../types'
import OutputPanel from './OutputPanel.vue'

interface Props {
  result: GenerateResponse | null
}

const props = defineProps<Props>()
const show = ref(false)

function triggerAnimation() {
  show.value = false
  // Force reflow so the browser registers the "from" state
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      show.value = true
    })
  })
}

// Watch for result changes (e.g., modify/re-generate)
watch(
  () => props.result,
  (val) => {
    if (val) {
      triggerAnimation()
    } else {
      show.value = false
    }
  },
)

// Handle first mount where result already exists
onMounted(() => {
  if (props.result) {
    triggerAnimation()
  }
})
</script>

<template>
  <div v-if="result" class="plan-display" :class="{ 'plan-display--visible': show }">
    <OutputPanel :result="result" />
  </div>
</template>

<style scoped>
.plan-display {
  opacity: 0;
  filter: blur(20px);
  transform: translateY(30px);
  transition:
    opacity 0.6s ease-out,
    filter 0.6s ease-out,
    transform 0.6s ease-out;
  max-width: 100%;
  margin: 0 auto;
  padding: var(--space-xl);
}

.plan-display--visible {
  opacity: 1;
  filter: blur(0);
  transform: translateY(0);
}
</style>
