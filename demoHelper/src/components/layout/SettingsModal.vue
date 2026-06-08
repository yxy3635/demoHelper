<script setup lang="ts">
import { ref } from 'vue'
import { useSettings } from '../../composables/useSettings'

const emit = defineEmits<{ close: [] }>()
const { settings, updateSettings, isConfigured } = useSettings()

const local = ref({ ...settings.value })
const showKey = ref(false)

const providers = [
  { value: 'deepseek', label: 'DeepSeek', desc: 'api.deepseek.com' },
  { value: 'openai', label: 'OpenAI', desc: 'api.openai.com' },
  { value: 'custom', label: '自定义', desc: '任意 OpenAI 兼容 API' },
] as const

function handleProviderChange(p: string) {
  local.value.provider = p as ApiSettings['provider']
  if (p === 'deepseek') {
    local.value.baseUrl = 'https://api.deepseek.com'
    local.value.model = 'deepseek-v4-flash'
  } else if (p === 'openai') {
    local.value.baseUrl = 'https://api.openai.com'
    local.value.model = 'gpt-4o'
  }
}

function handleSave() {
  updateSettings({
    provider: local.value.provider,
    apiKey: local.value.apiKey,
    baseUrl: local.value.baseUrl,
    model: local.value.model,
  })
  emit('close')
}

function handleCancel() {
  emit('close')
}
</script>

<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="handleCancel">
      <div class="modal-card" role="dialog" aria-label="API 设置">
        <div class="modal-header">
          <h3 class="modal-title">API 设置</h3>
          <p class="modal-desc">配置你的 AI API Key，将安全存储在你的浏览器本地。</p>
          <button class="modal-close" @click="handleCancel" aria-label="关闭">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- Provider -->
          <div class="form-group">
            <label class="form-label">服务商</label>
            <div class="provider-grid">
              <button
                v-for="p in providers" :key="p.value"
                class="provider-btn"
                :class="{ 'provider-btn--active': local.provider === p.value }"
                @click="handleProviderChange(p.value)"
                type="button"
              >
                <span class="provider-btn__name">{{ p.label }}</span>
                <span class="provider-btn__desc">{{ p.desc }}</span>
              </button>
            </div>
          </div>

          <!-- API Key -->
          <div class="form-group">
            <label class="form-label" for="settings-apikey">API Key</label>
            <div class="input-with-toggle">
              <input
                id="settings-apikey"
                v-model="local.apiKey"
                :type="showKey ? 'text' : 'password'"
                class="form-input"
                placeholder="sk-..."
                autocomplete="off"
              />
              <button class="toggle-vis" @click="showKey = !showKey" type="button" :aria-label="showKey ? '隐藏' : '显示'">
                <svg v-if="!showKey" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- Base URL -->
          <div class="form-group">
            <label class="form-label" for="settings-baseurl">接口地址</label>
            <input
              id="settings-baseurl"
              v-model="local.baseUrl"
              type="text"
              class="form-input"
              placeholder="https://api.deepseek.com"
            />
          </div>

          <!-- Model -->
          <div class="form-group">
            <label class="form-label" for="settings-model">模型</label>
            <input
              id="settings-model"
              v-model="local.model"
              type="text"
              class="form-input"
              placeholder="deepseek-v4-flash"
            />
          </div>
        </div>

        <div class="modal-footer">
          <span v-if="isConfigured()" class="configured-badge">已配置</span>
          <span v-else class="unconfigured-badge">未配置 API Key</span>
          <div class="modal-actions">
            <button class="btn-cancel" @click="handleCancel">取消</button>
            <button class="btn-save" @click="handleSave">保存</button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; z-index: var(--z-overlay);
  background: rgba(0,0,0,0.4); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  padding: var(--space-lg);
}

.modal-card {
  width: 100%; max-width: 460px; max-height: 90vh; overflow-y: auto;
  background: var(--bg); border: 1px solid var(--border); border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
}

.modal-header {
  padding: var(--space-xl) var(--space-xl) var(--space-md);
  position: relative;
}

.modal-title {
  font-size: var(--font-lg); font-weight: 700; margin: 0 0 4px; color: var(--text-h);
}

.modal-desc {
  font-size: var(--font-sm); color: var(--text); margin: 0;
}

.modal-close {
  position: absolute; top: var(--space-lg); right: var(--space-lg);
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
  border: none; background: transparent; color: var(--text); cursor: pointer;
  border-radius: var(--radius-sm); transition: background var(--transition-fast);
}
.modal-close:hover { background: var(--bg-secondary); }

.modal-body {
  padding: 0 var(--space-xl) var(--space-xl);
  display: flex; flex-direction: column; gap: var(--space-lg);
}

.form-group { display: flex; flex-direction: column; gap: 6px; }

.form-label {
  font-size: var(--font-sm); font-weight: 600; color: var(--text-h);
}

.form-input {
  width: 100%; padding: 10px 14px; border: 1.5px solid var(--border); border-radius: var(--radius-md);
  background: var(--bg-secondary); color: var(--text-h); font-size: var(--font-sm); font-family: var(--sans);
  transition: border-color var(--transition-fast);
}
.form-input:focus { outline: none; border-color: var(--accent-border); }

.input-with-toggle {
  display: flex; gap: 0;
}
.input-with-toggle .form-input { border-radius: var(--radius-md) 0 0 var(--radius-md); }
.toggle-vis {
  width: 40px; display: flex; align-items: center; justify-content: center;
  border: 1.5px solid var(--border); border-left: none; border-radius: 0 var(--radius-md) var(--radius-md) 0;
  background: var(--bg-secondary); color: var(--text); cursor: pointer;
}
.toggle-vis:hover { background: var(--border); }

.provider-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-sm);
}

.provider-btn {
  display: flex; flex-direction: column; align-items: center; gap: 2px;
  padding: var(--space-md) var(--space-sm); border: 1.5px solid var(--border);
  border-radius: var(--radius-md); background: var(--bg-secondary);
  cursor: pointer; transition: all var(--transition-fast);
}
.provider-btn:hover { border-color: var(--accent-border); }
.provider-btn--active {
  border-color: var(--accent); background: var(--accent-bg);
}

.provider-btn__name {
  font-size: var(--font-sm); font-weight: 600; color: var(--text-h);
}

.provider-btn__desc {
  font-size: 10px; color: var(--text); opacity: 0.6;
}

.modal-footer {
  padding: var(--space-md) var(--space-xl) var(--space-xl);
  display: flex; align-items: center; justify-content: space-between;
}

.configured-badge {
  font-size: var(--font-xs); color: var(--color-success); font-weight: 600;
}

.unconfigured-badge {
  font-size: var(--font-xs); color: var(--color-error); font-weight: 600;
}

.modal-actions { display: flex; gap: var(--space-sm); }

.btn-cancel, .btn-save {
  padding: 10px 20px; border-radius: var(--radius-md);
  font-size: var(--font-sm); font-weight: 600; cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-cancel {
  border: 1.5px solid var(--border); background: var(--bg); color: var(--text);
}
.btn-cancel:hover { background: var(--bg-secondary); }

.btn-save {
  border: none;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
}
.btn-save:hover { box-shadow: 0 4px 14px var(--accent-border); }
</style>
