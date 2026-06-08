<script setup lang="ts">
import { ref, watch, onUnmounted } from 'vue'
import AppHeader from './components/layout/AppHeader.vue'
import GuidedInput from './components/input/GuidedInput.vue'
import PlanDisplay from './components/output/PlanDisplay.vue'
import ModifyPanel from './components/layout/ModifyPanel.vue'
import ErrorMessage from './components/shared/ErrorMessage.vue'
import LoadingSpinner from './components/shared/LoadingSpinner.vue'
import { useGenerator } from './composables/useGenerator'

const { result, isLoading, error, progressStep, generate, reset } = useGenerator()

// ── Progress step labels ──
const PROGRESS_STEPS = [
  '正在连接 AI 服务...',
  'AI 正在分析需求并生成计划...',
  '正在解析和整理输出结果...',
  '即将完成...',
]

// ── Elapsed time counter ──
const elapsedSeconds = ref(0)
let timerInterval: ReturnType<typeof setInterval> | null = null

watch(isLoading, (val) => {
  if (val) {
    elapsedSeconds.value = 0
    timerInterval = setInterval(() => { elapsedSeconds.value++ }, 1000)
  } else {
    if (timerInterval) { clearInterval(timerInterval); timerInterval = null }
  }
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
})

// ── Generate logic ──
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
  if (lastPayload) handleGenerate(lastPayload)
}

function handleModify(feedback: string) {
  let modifyIntent = `**修改请求**: ${feedback}\n\n`

  if (lastPayload) {
    modifyIntent += `**原始项目描述**:\n${lastPayload.intentText}\n\n`
    if (lastPayload.techStack) {
      modifyIntent += `**原始技术栈**: ${lastPayload.techStack}\n\n`
    }
  }

  if (result.value) {
    modifyIntent += `**上一版计划概览**:\n`
    modifyIntent += `- 标题: ${result.value.title}\n`
    modifyIntent += `- 共 ${result.value.sections.length} 个章节\n`
    result.value.sections.forEach(s => {
      modifyIntent += `  - ${s.title}\n`
    })
    modifyIntent += `\n`
  }

  modifyIntent += `请基于以上原始需求，根据修改请求重新生成完整的开发计划。`
  generate(modifyIntent, null)
}

function handleNewPlan() {
  reset()
}

// ── Layout mode ──
const hasExistingResult = ref(false)
watch(result, (val) => {
  if (val) hasExistingResult.value = true
})
function onReset() {
  hasExistingResult.value = false
  reset()
}
</script>

<template>
  <div class="app-shell">
    <AppHeader :show-new-plan="result !== null" @new-plan="onReset" />

    <!-- ===== FIRST-TIME LAYOUT: centered, input → loading → result ===== -->
    <div v-if="!hasExistingResult" class="app-body-first">
      <main class="app-main-first">
        <!-- Loading -->
        <section v-if="isLoading" class="first-loading" aria-live="polite">
          <LoadingSpinner />
          <div class="loading-info">
            <TransitionGroup name="step-fade" tag="div" class="progress-steps">
              <div
                v-for="idx in progressStep + 1"
                :key="idx"
                class="progress-step"
                :class="{
                  'progress-step--done': idx < progressStep,
                  'progress-step--active': idx === progressStep,
                }"
              >
                <span class="progress-step__dot"></span>
                <span class="progress-step__label">{{ PROGRESS_STEPS[idx] }}</span>
              </div>
            </TransitionGroup>
            <p class="elapsed-time">已等待 {{ elapsedSeconds }} 秒</p>
          </div>
        </section>

        <!-- Error -->
        <section v-else-if="error" class="first-section" aria-live="assertive">
          <ErrorMessage :message="error" @retry="handleRetry" />
        </section>

        <!-- Input (idle) -->
        <section v-else-if="!result" class="first-input">
          <div class="guided-input-wrapper">
            <div class="input-header-center">
              <div class="input-header__icon">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
                </svg>
              </div>
              <h2 class="input-header__title">描述你的项目</h2>
              <p class="input-header__sub">填写以下信息，AI 将生成完整的开发计划</p>
            </div>
            <GuidedInput :loading="false" @generate="handleGenerate" />
          </div>
        </section>

        <!-- Result (replaces input on first submit) -->
        <PlanDisplay v-else :result="result" />
      </main>
    </div>

    <!-- ===== MODIFICATION LAYOUT: main content + right sidebar (adjust only) ===== -->
    <div v-else class="app-body-split">
      <main class="mod-main">
        <!-- Loading (modification) -->
        <section v-if="isLoading" class="mod-loading" aria-live="polite">
          <LoadingSpinner />
          <div class="loading-info">
            <TransitionGroup name="step-fade" tag="div" class="progress-steps">
              <div
                v-for="idx in progressStep + 1"
                :key="idx"
                class="progress-step"
                :class="{
                  'progress-step--done': idx < progressStep,
                  'progress-step--active': idx === progressStep,
                }"
              >
                <span class="progress-step__dot"></span>
                <span class="progress-step__label">{{ PROGRESS_STEPS[idx] }}</span>
              </div>
            </TransitionGroup>
            <p class="elapsed-time">已等待 {{ elapsedSeconds }} 秒</p>
          </div>
        </section>

        <!-- Error (modification) -->
        <section v-else-if="error" class="mod-error" aria-live="assertive">
          <ErrorMessage :message="error" @retry="handleRetry" />
        </section>

        <!-- Result (modification) -->
        <PlanDisplay v-else-if="result" :result="result" />
      </main>

      <!-- Right Sidebar: only adjust panel after first result -->
      <aside class="mod-sidebar">
        <ModifyPanel
          :loading="isLoading"
          :has-result="true"
          @modify="handleModify"
        />
      </aside>
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

/* ===== FIRST-TIME LAYOUT ===== */
.app-body-first {
  flex: 1;
  overflow-y: auto;
}

.app-main-first {
  min-height: 100%;
}

.first-input {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-xl) 0;
}

.input-header-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: var(--space-2xl);
  padding: 0 var(--space-xl);
}

.input-header__icon {
  width: 52px; height: 52px; display: flex; align-items: center; justify-content: center;
  background: var(--accent-bg); border-radius: var(--radius-lg); flex-shrink: 0;
  margin-bottom: var(--space-md);
}

.input-header__title {
  font-size: var(--font-2xl); font-weight: 700; margin: 0;
}

.input-header__sub {
  font-size: var(--font-base); color: var(--text); margin: var(--space-xs) 0 0;
}

.first-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.first-section {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-xl);
}

/* ===== MODIFICATION LAYOUT ===== */
.app-body-split {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.mod-main {
  flex: 1;
  overflow-y: auto;
  min-width: 0;
  background:
    radial-gradient(ellipse 80% 50% at 50% -10%, var(--accent-bg), transparent),
    var(--bg);
}

.mod-sidebar {
  width: 360px;
  flex-shrink: 0;
  border-left: 1px solid var(--border);
  background: var(--bg);
  overflow-y: auto;
}

.mod-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100%;
}

.mod-error {
  max-width: 720px;
  margin: 0 auto;
  padding: var(--space-2xl) var(--space-xl);
}

/* ===== Shared Loading / Progress ===== */
.loading-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-lg);
  margin-top: var(--space-xl);
}

.progress-steps {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  min-width: 260px;
}

.progress-step {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: 10px 16px;
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
}

.progress-step__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--border);
  flex-shrink: 0;
  transition: all var(--transition-base);
}

.progress-step__label {
  font-size: var(--font-sm);
  color: var(--text);
  transition: color var(--transition-base);
}

.progress-step--active .progress-step__dot {
  background: var(--accent);
  box-shadow: 0 0 8px var(--accent-border);
  animation: pulse-dot 1.2s ease-in-out infinite;
}

.progress-step--active .progress-step__label {
  color: var(--accent);
  font-weight: 600;
}

.progress-step--done .progress-step__dot {
  background: var(--color-success);
}

.progress-step--done .progress-step__label {
  color: var(--color-success);
}

@keyframes pulse-dot {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.6; }
}

.elapsed-time {
  font-size: var(--font-sm);
  color: var(--text);
  opacity: 0.7;
  margin: 0;
  font-variant-numeric: tabular-nums;
}

/* Step fade-in transition */
.step-fade-enter-active {
  transition: all 0.4s var(--transition-spring);
}
.step-fade-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Responsive ===== */
@media (max-width: 1024px) {
  .app-body-split {
    flex-direction: column;
  }

  .mod-sidebar {
    width: 100%;
    max-height: 40vh;
    border-left: none;
    border-top: 1px solid var(--border);
  }

  .mod-main {
    min-height: 50vh;
  }
}
</style>
