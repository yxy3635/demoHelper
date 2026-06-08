<script setup lang="ts">
import { ref } from 'vue'
import { useTheme } from '../../composables/useTheme'
import SettingsModal from './SettingsModal.vue'

interface Props { showNewPlan?: boolean }
defineProps<Props>()
const emit = defineEmits<{ 'newPlan': [] }>()
const { theme, toggleTheme } = useTheme()

const showSettings = ref(false)
</script>

<template>
  <header class="app-header">
    <div class="app-header__inner">
      <div class="app-header__left">
        <div class="logo">
          <svg width="28" height="28" viewBox="0 0 36 36" fill="none">
            <defs>
              <linearGradient id="logo-grad" x1="0" y1="0" x2="36" y2="36">
                <stop offset="0%" stop-color="var(--accent)"/>
                <stop offset="100%" stop-color="var(--accent-2)"/>
              </linearGradient>
            </defs>
            <rect width="36" height="36" rx="9" fill="url(#logo-grad)"/>
            <path d="M9 18l6-6 6 6 6-6" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 24l6-6 6 6 6-6" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" opacity="0.5"/>
          </svg>
        </div>
        <div class="brand">
          <span class="brand-name">VibeCoding</span>
          <span class="brand-tag">AI development planner</span>
        </div>
      </div>

      <div class="app-header__right">
        <button v-if="showNewPlan" class="btn-new" @click="emit('newPlan')">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          新计划
        </button>
        <button class="btn-settings" @click="showSettings = true" title="API 设置" aria-label="API 设置">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>
          </svg>
        </button>
        <label class="switch" :title="theme === 'dark' ? '暗色模式' : '亮色模式'" :aria-label="theme === 'dark' ? '切换到亮色模式' : '切换到暗色模式'">
          <span class="sun">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><g fill="#ffd43b"><circle r="5" cy="12" cx="12"/><path d="m21 13h-1a1 1 0 0 1 0-2h1a1 1 0 0 1 0 2zm-17 0h-1a1 1 0 0 1 0-2h1a1 1 0 0 1 0 2zm13.66-5.66a1 1 0 0 1 -.66-.29 1 1 0 0 1 0-1.41l.71-.71a1 1 0 1 1 1.41 1.41l-.71.71a1 1 0 0 1 -.75.29zm-12.02 12.02a1 1 0 0 1 -.71-.29 1 1 0 0 1 0-1.41l.71-.66a1 1 0 0 1 1.41 1.41l-.71.71a1 1 0 0 1 -.7.24zm6.36-14.36a1 1 0 0 1 -1-1v-1a1 1 0 0 1 2 0v1a1 1 0 0 1 -1 1zm0 17a1 1 0 0 1 -1-1v-1a1 1 0 0 1 2 0v1a1 1 0 0 1 -1 1zm-5.66-14.66a1 1 0 0 1 -.7-.29l-.71-.71a1 1 0 0 1 1.41-1.41l.71.71a1 1 0 0 1 0 1.41 1 1 0 0 1 -.71.29zm12.02 12.02a1 1 0 0 1 -.7-.29l-.66-.71a1 1 0 0 1 1.36-1.36l.71.71a1 1 0 0 1 0 1.41 1 1 0 0 1 -.71.24z"/></g></svg>
          </span>
          <span class="moon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="m223.5 32c-123.5 0-223.5 100.3-223.5 224s100 224 223.5 224c60.6 0 115.5-24.2 155.8-63.4 5-4.9 6.3-12.5 3.1-18.7s-10.1-9.7-17-8.5c-9.8 1.7-19.8 2.6-30.1 2.6-96.9 0-175.5-78.8-175.5-176 0-65.8 36-123.1 89.3-153.3 6.1-3.5 9.2-10.5 7.7-17.3s-7.3-11.9-14.3-12.5c-6.3-.5-12.6-.8-19-.8z"/></svg>
          </span>
          <input type="checkbox" class="input" :checked="theme === 'dark'" @change="toggleTheme"/>
          <span class="slider"></span>
        </label>
      </div>
    </div>
  </header>
  <SettingsModal v-if="showSettings" @close="showSettings = false" />
</template>

<style scoped>
.app-header {
  position: sticky; top: 0; z-index: var(--z-sticky);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  background: color-mix(in srgb, var(--bg) 80%, transparent);
  border-bottom: 1px solid var(--border);
}

.app-header__inner {
  max-width: 1280px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 var(--space-xl); height: 56px;
}

.app-header__left { display: flex; align-items: center; gap: var(--space-md); }
.logo { display: flex; align-items: center; }
.brand { display: flex; flex-direction: column; gap: 0; }
.brand-name { font-size: 17px; font-weight: 700; color: var(--text-h); letter-spacing: -0.3px; }
.brand-tag { font-size: 11px; color: var(--text); opacity: 0.5; font-weight: 500; }

.app-header__right { display: flex; align-items: center; gap: var(--space-md); }

.btn-new {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 7px 16px;
  border: 1px solid var(--accent-border); border-radius: var(--radius-full);
  background: var(--accent-bg); color: var(--accent);
  font-size: 13px; font-weight: 600; cursor: pointer;
  transition: all var(--transition-fast);
}
.btn-new:hover { background: var(--accent); color: #fff; box-shadow: 0 4px 12px var(--accent-border); }

.btn-settings {
  display: flex; align-items: center; justify-content: center;
  width: 36px; height: 36px; border: none; border-radius: var(--radius-md);
  background: transparent; color: var(--text); cursor: pointer;
  transition: all var(--transition-fast);
}
.btn-settings:hover { background: var(--bg-secondary); color: var(--text-h); }

/* Switch */
.switch { font-size: 17px; position: relative; display: inline-block; width: 60px; height: 32px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; inset: 0; background-color: #73C0FC; transition: .4s; border-radius: 30px; }
.slider:before { position: absolute; content: ""; height: 28px; width: 28px; border-radius: 20px; left: 2px; bottom: 2px; z-index: 2; background-color: #e8e8e8; transition: .4s; }
.sun svg { position: absolute; top: 5px; left: 34px; z-index: 1; width: 22px; height: 22px; }
.moon svg { fill: #73C0FC; position: absolute; top: 4px; left: 5px; z-index: 1; width: 22px; height: 22px; }
.switch:hover .sun svg { animation: rotate 15s linear infinite; }
.switch:hover .moon svg { animation: tilt 5s linear infinite; }
@keyframes rotate { 0% { transform: rotate(0); } 100% { transform: rotate(360deg); } }
@keyframes tilt { 0% { transform: rotate(0); } 25% { transform: rotate(-10deg); } 75% { transform: rotate(10deg); } 100% { transform: rotate(0); } }
.input:checked + .slider { background-color: #183153; }
.input:focus + .slider { box-shadow: 0 0 1px #183153; }
.input:checked + .slider:before { transform: translateX(28px); }

@media (max-width: 768px) {
  .app-header__inner { padding: 0 var(--space-md); }
  .brand-tag { display: none; }
}
</style>
