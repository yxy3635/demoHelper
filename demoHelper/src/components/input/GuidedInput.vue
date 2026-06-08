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
      <h3 class="input-header__title">项目需求</h3>
      <span class="input-header__badge">必填 *</span>
    </div>

    <div class="form-group">
      <input id="project-name" v-model="projectName" type="text" class="form-input" placeholder="项目名称（可选）" :disabled="loading"/>
    </div>

    <div class="form-group">
      <textarea id="intent-text" v-model="intentText" class="form-textarea" rows="10"
        placeholder="详细描述你的项目需求...&#10;&#10;示例：&#10;我想做一个在线五子棋游戏：&#10;- 前端 Vue3 + TypeScript&#10;- 后端 Java Spring Boot + MyBatis&#10;- MySQL 存储用户和房间状态&#10;- WebSocket 实时同步对战"
        :disabled="loading"/>
      <span class="form-hint">{{ intentText.length }}/5000</span>
    </div>

    <div class="form-advanced">
      <button class="form-advanced-toggle" @click="showAdvanced = !showAdvanced" type="button">
        <svg class="toggle-chevron" :class="{ 'toggle-chevron--open': showAdvanced }" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="6 9 12 15 18 9"/>
        </svg>
        高级选项
      </button>
      <div v-if="showAdvanced" class="form-advanced-body">
        <div class="form-group">
          <label class="form-label" for="tech-stack">技术栈偏好</label>
          <input id="tech-stack" v-model="techStack" type="text" class="form-input" placeholder="例如：Vue3, Spring Boot, PostgreSQL..." :disabled="loading"/>
        </div>
        <div class="form-group">
          <label class="form-label">参考图片</label>
          <div v-if="imageBase64" class="image-upload-area"><ImagePreview :image-base64="imageBase64" :image-name="imageName" @remove="removeImage"/></div>
          <div v-else class="drop-area" @click="openFilePicker()" role="button" tabindex="0" @keydown.enter="openFilePicker()" @keydown.space.prevent="openFilePicker()">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
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
        <span class="generate-spinner"></span>生成中...
      </template>
      <template v-else>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
        </svg>
        生成开发计划
      </template>
    </button>
  </div>
</template>

<style scoped>
.guided-input {
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.input-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.input-header__title {
  font-size: var(--font-lg);
  font-weight: 700;
  margin: 0;
  color: var(--text-h);
}

.input-header__badge {
  font-size: var(--font-xs);
  color: var(--color-error);
  font-weight: 600;
  background: var(--color-error-bg);
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

.form-group { margin-bottom: 0; }

.form-label { display: block; font-size: var(--font-xs); font-weight: 600; color: var(--text-h); margin-bottom: 4px; }

.form-input {
  width: 100%; padding: 10px 14px; border: 1.5px solid var(--border); border-radius: var(--radius-md);
  background: var(--bg-secondary); color: var(--text-h); font-size: var(--font-sm); font-family: var(--sans);
  transition: all var(--transition-fast);
}
.form-input:focus { outline: none; border-color: var(--accent-border); background: var(--bg); box-shadow: 0 0 0 3px var(--accent-bg); }

.form-textarea {
  width: 100%; padding: 12px 14px; border: 1.5px solid var(--border); border-radius: var(--radius-md);
  background: var(--bg-secondary); color: var(--text-h); font-size: var(--font-sm); font-family: var(--sans);
  line-height: 1.6; resize: vertical; min-height: 200px;
  transition: all var(--transition-fast);
}
.form-textarea:focus { outline: none; border-color: var(--accent-border); background: var(--bg); box-shadow: 0 0 0 3px var(--accent-bg); }
.form-textarea::placeholder { color: var(--text); opacity: 0.4; font-size: var(--font-sm); }
.form-hint { display: block; margin-top: 4px; font-size: var(--font-xs); color: var(--text); opacity: 0.5; text-align: right; }
.form-error { margin: var(--space-xs) 0 0; font-size: var(--font-xs); color: var(--color-error); }

.form-advanced { border: 1px solid var(--border); border-radius: var(--radius-md); overflow: hidden; }
.form-advanced-toggle {
  display: flex; align-items: center; gap: 6px; width: 100%;
  padding: 10px 14px; border: none; background: var(--bg-secondary);
  color: var(--text); font-size: var(--font-sm); font-weight: 600; cursor: pointer;
  transition: background var(--transition-fast);
}
.form-advanced-toggle:hover { background: var(--border); }
.toggle-chevron { transition: transform var(--transition-fast); flex-shrink: 0; }
.toggle-chevron--open { transform: rotate(180deg); }
.form-advanced-body { padding: var(--space-md) var(--space-md) var(--space-sm); border-top: 1px solid var(--border); background: var(--bg); }

.drop-area {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: var(--space-md); border: 2px dashed var(--border); border-radius: var(--radius-md);
  cursor: pointer; transition: all var(--transition-fast); color: var(--text); font-size: var(--font-sm);
}
.drop-area:hover { border-color: var(--accent-border); background: var(--accent-bg); color: var(--accent); }
.drop-hint { font-size: var(--font-xs); opacity: 0.45; }
.image-upload-area { max-width: 280px; }

.generate-btn {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  width: 100%; padding: 13px 24px; border: none; border-radius: var(--radius-md);
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff; font-size: var(--font-base); font-weight: 700; cursor: pointer;
  box-shadow: 0 4px 16px var(--accent-border);
  transition: all var(--transition-base);
}
.generate-btn:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 6px 24px var(--accent-border); }
.generate-btn:active:not(:disabled) { transform: translateY(0); }
.generate-btn:disabled { opacity: 0.4; cursor: not-allowed; box-shadow: none; }
.generate-spinner { width: 16px; height: 16px; border: 2.5px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>
