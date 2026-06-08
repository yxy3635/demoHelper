<script setup lang="ts">
import { ref } from 'vue'

interface Emits { modify: [text: string] }
const emit = defineEmits<Emits>()
interface Props { loading: boolean; hasResult: boolean }
defineProps<Props>()

const feedback = ref('')
const history = ref<{ role: 'user' | 'system'; text: string }[]>([])

function handleSend() {
  const text = feedback.value.trim()
  if (!text) return
  history.value.push({ role: 'user', text })
  emit('modify', text)
  feedback.value = ''
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleSend() }
}
</script>

<template>
  <aside class="modify-panel">
    <div class="modify-panel__header">
      <div class="modify-panel__header-icon">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
        </svg>
      </div>
      <div>
        <h3 class="modify-panel__title">调整计划</h3>
        <p class="modify-panel__desc">对生成结果不满意？描述需要修改的内容。</p>
      </div>
    </div>

    <div class="modify-panel__history" v-if="history.length > 0">
      <div v-for="(msg, idx) in history" :key="idx" class="msg" :class="`msg--${msg.role}`">
        <div class="msg__role">{{ msg.role === 'user' ? '你' : '系统' }}</div>
        <div class="msg__text">{{ msg.text }}</div>
      </div>
    </div>
    <div v-else class="modify-panel__empty">
      <div class="empty-illustration">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" opacity="0.3">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          <line x1="9" y1="10" x2="15" y2="10"/><line x1="12" y1="7" x2="12" y2="13"/>
        </svg>
      </div>
      <p>在此输入修改意见，AI 将根据反馈调整计划。</p>
    </div>

    <div class="modify-panel__input">
      <textarea v-model="feedback" class="modify-input" rows="2"
        placeholder="例如：增加测试阶段、使用 PostgreSQL..."
        :disabled="loading" @keydown="handleKeydown"/>
      <button class="modify-send" :disabled="!feedback.trim() || loading" @click="handleSend" aria-label="发送修改请求">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
        </svg>
      </button>
    </div>
  </aside>
</template>

<style scoped>
.modify-panel {
  display: flex; flex-direction: column; height: 100%;
  background: var(--bg-secondary); border-left: 1px solid var(--border);
}

.modify-panel__header {
  display: flex; gap: var(--space-md); padding: var(--space-lg);
  border-bottom: 1px solid var(--border); background: var(--bg);
}
.modify-panel__header-icon {
  width: 40px; height: 40px; display: flex; align-items: center; justify-content: center;
  background: var(--accent-bg); border-radius: var(--radius-md); flex-shrink: 0;
}
.modify-panel__title { font-size: var(--font-base); font-weight: 700; margin: 0; }
.modify-panel__desc { font-size: var(--font-xs); color: var(--text); margin: 2px 0 0; }

.modify-panel__history { flex: 1; overflow-y: auto; padding: var(--space-md); display: flex; flex-direction: column; gap: var(--space-sm); }
.msg { padding: var(--space-md); border-radius: var(--radius-md); }
.msg--user { background: var(--accent-bg); border: 1px solid var(--accent-border); }
.msg--system { background: var(--bg); border: 1px solid var(--border); }
.msg__role { font-size: 10px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; color: var(--accent); margin-bottom: 4px; }
.msg__text { font-size: var(--font-sm); color: var(--text-h); line-height: 1.5; }

.modify-panel__empty {
  flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: var(--space-xl); text-align: center; gap: var(--space-md);
}
.modify-panel__empty p { font-size: var(--font-sm); color: var(--text); opacity: 0.5; }

.modify-panel__input {
  display: flex; gap: var(--space-sm); padding: var(--space-md);
  border-top: 1px solid var(--border); background: var(--bg);
  align-items: flex-end;
}
.modify-input {
  flex: 1; padding: 10px 14px; border: 1.5px solid var(--border); border-radius: var(--radius-md);
  background: var(--bg-secondary); color: var(--text-h); font-family: var(--sans);
  font-size: var(--font-sm); line-height: 1.5; resize: none; outline: none;
  min-height: 44px; transition: border-color var(--transition-fast);
}
.modify-input:focus { border-color: var(--accent-border); }
.modify-input::placeholder { color: var(--text); opacity: 0.4; }
.modify-send {
  display: flex; align-items: center; justify-content: center;
  width: 40px; height: 40px; border: none; border-radius: var(--radius-md);
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff; cursor: pointer; flex-shrink: 0; transition: all var(--transition-fast);
}
.modify-send:disabled { opacity: 0.25; cursor: not-allowed; }
.modify-send:hover:not(:disabled) { box-shadow: 0 4px 14px var(--accent-border); }
</style>
