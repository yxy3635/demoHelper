<script setup lang="ts">
import AppHeader from './components/layout/AppHeader.vue'
import GuidedInput from './components/input/GuidedInput.vue'
import PlanDisplay from './components/output/PlanDisplay.vue'
import ModifyPanel from './components/layout/ModifyPanel.vue'
import ErrorMessage from './components/shared/ErrorMessage.vue'
import LoadingSpinner from './components/shared/LoadingSpinner.vue'
import { useGenerator } from './composables/useGenerator'

const { result, isLoading, error, generate, reset } = useGenerator()

// Cache last payload so retry can re-submit
let lastPayload: {
  projectName: string
  intentText: string
  techStack: string
  imageBase64: string | null
} | null = null

async function handleGenerate(payload: {
  projectName: string
  intentText: string
  techStack: string
  imageBase64: string | null
}) {
  lastPayload = { ...payload, imageBase64: payload.imageBase64 }
  let fullIntent = payload.intentText
  if (payload.projectName.trim()) {
    fullIntent = `**项目名称:** ${payload.projectName.trim()}\n\n${fullIntent}`
  }
  if (payload.techStack.trim()) {
    fullIntent += `\n\n**技术栈偏好:** ${payload.techStack.trim()}`
  }
  await generate(fullIntent, payload.imageBase64)
}

function handleRetry() {
  if (lastPayload) {
    handleGenerate(lastPayload)
  }
}

function handleModify(feedback: string) {
  const modifyIntent = `[修改请求]: ${feedback}`
  generate(modifyIntent, null)
}

function handleNewPlan() {
  reset()
}
</script>

<template>
  <div class="app-shell">
    <AppHeader :show-new-plan="result !== null" @new-plan="handleNewPlan" />
    <div class="app-body">
      <!-- Left: Input (initial) or Result (after generation) -->
      <main class="app-main">
        <!-- Loading State -->
        <section v-if="isLoading" class="app-loading" aria-live="polite">
          <LoadingSpinner label="正在分析需求，生成开发计划..." />
          <p class="loading-sub">调用 AI 模型中，请稍候</p>
        </section>

        <!-- Error State -->
        <section v-else-if="error" class="app-section" aria-live="assertive">
          <ErrorMessage :message="error" @retry="handleRetry" />
        </section>

        <!-- Initial: Input Form -->
        <section v-else-if="!result" class="app-input" aria-label="项目输入">
          <GuidedInput :loading="false" @generate="handleGenerate" />
        </section>

        <!-- Result: Plan Display (replaces input) -->
        <PlanDisplay v-else :result="result" />
      </main>

      <!-- Right: Modification Sidebar (only after result exists) -->
      <ModifyPanel
        v-if="result"
        :loading="isLoading"
        :has-result="true"
        @modify="handleModify"
      />
    </div>
  </div>
</template>

<style scoped>
.app-shell {
  height: 100svh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.app-main {
  flex: 1;
  overflow-y: auto;
  min-width: 0;
}

.app-input {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-xl) 0;
}

.app-loading {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.loading-sub {
  font-size: var(--font-sm);
  color: var(--text);
  margin: 0;
}

.app-section {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-xl);
}

/* Right sidebar */
.app-body > :last-child {
  width: 360px;
  flex-shrink: 0;
}

@media (max-width: 1024px) {
  .app-body {
    flex-direction: column;
  }

  .app-body > :last-child {
    width: 100%;
    max-height: 40vh;
  }
}

@media (max-width: 768px) {
  .app-input {
    padding: var(--space-md) 0;
  }
}
</style>
