<script setup lang="ts">
import { ref, computed } from 'vue'
import { useImageUpload } from '../../composables/useImageUpload'
import ImagePreview from './ImagePreview.vue'

interface Emits {
  generate: [payload: { intentText: string; imageBase64: string | null; projectName: string; techStack: string }]
}
const emit = defineEmits<Emits>()
interface Props { loading: boolean }
defineProps<Props>()

const projectName = ref('')
const intentText = ref('')
const techStack = ref('')
const showAdvanced = ref(false)

const { imageBase64, imageName, error: imageError, openFilePicker, removeImage } = useImageUpload()
const canGenerate = computed(() => intentText.value.trim().length > 0)

function handleGenerate() {
  if (!canGenerate.value) return
  emit('generate', { projectName: projectName.value, intentText: intentText.value, techStack: techStack.value, imageBase64: imageBase64.value })
}
</script>

<template>
  <div class="guided-input">
    <div class="input-header">
      <div class="input-header__icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/>
        </svg>
      </div>
      <div>
        <h2 class="input-header__title">描述你的项目</h2>
        <p class="input-header__sub">填写以下信息，AI 将生成完整的开发计划，包含可直接复制使用的提示词。</p>
      </div>
    </div>

    <div class="form-card">
      <div class="form-group">
        <label class="form-label" for="project-name">项目名称</label>
        <input id="project-name" v-model="projectName" type="text" class="form-input" placeholder="例如：React 待办事项应用、电商 API 服务..." :disabled="loading"/>
      </div>

      <div class="form-group">
        <label class="form-label" for="intent-text">项目需求 <span class="required">*</span></label>
        <textarea id="intent-text" v-model="intentText" class="form-textarea" rows="7"
          placeholder="尽可能详细地描述你的项目需求，描述越具体，生成的计划越精准。&#10;&#10;示例：&#10;我想构建一个全栈待办事项应用：&#10;- React 18 + TypeScript 前端，Tailwind CSS 样式&#10;- Firebase 认证（邮箱 + Google 登录）&#10;- Firestore 实时数据同步&#10;- 支持创建、编辑、删除、排序任务&#10;- 移动端响应式 + 暗色模式"
          :disabled="loading"/>
        <span class="form-hint">支持 Markdown 格式 &middot; {{ intentText.length }}/5000</span>
      </div>
    </div>

    <div class="form-advanced">
      <button class="form-advanced-toggle" @click="showAdvanced = !showAdvanced" type="button">
        <svg class="toggle-chevron" :class="{ 'toggle-chevron--open': showAdvanced }" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="6 9 12 15 18 9"/>
        </svg>
        高级选项
      </button>
      <div v-if="showAdvanced" class="form-advanced-body">
        <div class="form-group">
          <label class="form-label" for="tech-stack">技术栈偏好</label>
          <input id="tech-stack" v-model="techStack" type="text" class="form-input" placeholder="例如：React, Python FastAPI, PostgreSQL, Vercel..." :disabled="loading"/>
        </div>
        <div class="form-group">
          <label class="form-label">参考图片 / 设计稿</label>
          <div v-if="imageBase64" class="image-upload-area"><ImagePreview :image-base64="imageBase64" :image-name="imageName" @remove="removeImage"/></div>
          <div v-else class="drop-area" @click="openFilePicker()" role="button" tabindex="0" @keydown.enter="openFilePicker()" @keydown.space.prevent="openFilePicker()">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/>
            </svg>
            <span>点击上传设计参考图</span>
            <span class="drop-hint">PNG、JPEG、GIF、WebP &middot; 最大 10MB</span>
          </div>
          <p v-if="imageError" class="form-error">{{ imageError }}</p>
        </div>
      </div>
    </div>

    <button class="generate-btn" :class="{ 'generate-btn--loading': loading }" :disabled="!canGenerate || loading" @click="handleGenerate">
      <template v-if="loading">
        <span class="generate-spinner"></span>正在生成计划...
      </template>
      <template v-else>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
        </svg>
        生成开发计划
      </template>
    </button>
  </div>
</template>

<style scoped>
.guided-input { max-width: 720px; margin: 0 auto; padding: var(--space-2xl) var(--space-xl); }

.input-header { display: flex; gap: var(--space-md); margin-bottom: var(--space-xl); }
.input-header__icon {
  width: 52px; height: 52px; display: flex; align-items: center; justify-content: center;
  background: var(--accent-bg); border-radius: var(--radius-lg); flex-shrink: 0;
}
.input-header__title { font-size: var(--font-2xl); font-weight: 700; margin: 0; }
.input-header__sub { font-size: var(--font-base); color: var(--text); line-height: 1.5; margin: var(--space-xs) 0 0; }

.form-card {
  background: var(--bg); border: 1px solid var(--border); border-radius: var(--radius-lg);
  padding: var(--space-xl); box-shadow: var(--shadow-sm); margin-bottom: var(--space-md);
}

.form-group { margin-bottom: var(--space-lg); }
.form-group:last-child { margin-bottom: 0; }
.form-label { display: block; font-size: var(--font-sm); font-weight: 600; color: var(--text-h); margin-bottom: 8px; }
.required { color: var(--color-error); }

.form-input {
  width: 100%; padding: 11px 16px; border: 1.5px solid var(--border); border-radius: var(--radius-md);
  background: var(--bg-secondary); color: var(--text-h); font-size: var(--font-base); font-family: var(--sans);
  transition: all var(--transition-fast);
}
.form-input:focus { outline: none; border-color: var(--accent-border); background: var(--bg); box-shadow: 0 0 0 4px var(--accent-bg); }

.form-textarea {
  width: 100%; padding: 14px 16px; border: 1.5px solid var(--border); border-radius: var(--radius-md);
  background: var(--bg-secondary); color: var(--text-h); font-size: var(--font-base); font-family: var(--sans);
  line-height: 1.65; resize: vertical; min-height: 170px;
  transition: all var(--transition-fast);
}
.form-textarea:focus { outline: none; border-color: var(--accent-border); background: var(--bg); box-shadow: 0 0 0 4px var(--accent-bg); }
.form-textarea::placeholder { color: var(--text); opacity: 0.45; }
.form-hint { display: block; margin-top: 6px; font-size: var(--font-xs); color: var(--text); opacity: 0.5; }
.form-error { margin: var(--space-xs) 0 0; font-size: var(--font-sm); color: var(--color-error); }

.form-advanced { margin-bottom: var(--space-xl); border: 1px solid var(--border); border-radius: var(--radius-lg); overflow: hidden; }
.form-advanced-toggle { display: flex; align-items: center; gap: var(--space-sm); width: 100%; padding: 12px 18px; border: none; background: var(--bg-secondary); color: var(--text); font-size: var(--font-sm); font-weight: 600; cursor: pointer; transition: background var(--transition-fast); }
.form-advanced-toggle:hover { background: var(--border); }
.toggle-chevron { transition: transform var(--transition-fast); }
.toggle-chevron--open { transform: rotate(180deg); }
.form-advanced-body { padding: var(--space-lg) var(--space-lg) var(--space-md); border-top: 1px solid var(--border); background: var(--bg); }

.drop-area {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: var(--space-xl); border: 2px dashed var(--border); border-radius: var(--radius-md);
  cursor: pointer; transition: all var(--transition-fast); color: var(--text); font-size: var(--font-sm);
}
.drop-area:hover { border-color: var(--accent-border); background: var(--accent-bg); color: var(--accent); }
.drop-hint { font-size: var(--font-xs); opacity: 0.45; }
.image-upload-area { max-width: 360px; }

.generate-btn {
  display: inline-flex; align-items: center; gap: 10px;
  padding: 15px 44px; border: none; border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff; font-size: var(--font-base); font-weight: 700; cursor: pointer;
  box-shadow: 0 4px 16px var(--accent-border);
  transition: all var(--transition-base);
}
.generate-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 28px var(--accent-border); }
.generate-btn:active:not(:disabled) { transform: translateY(0); }
.generate-btn:disabled { opacity: 0.45; cursor: not-allowed; box-shadow: none; }
.generate-spinner { width: 18px; height: 18px; border: 2.5px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 768px) {
  .guided-input { padding: var(--space-lg) var(--space-md); }
  .input-header { flex-direction: column; }
}
</style>
