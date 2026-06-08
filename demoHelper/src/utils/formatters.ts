import type { Difficulty } from '../types'

export function formatHours(hours: number): string {
  if (hours < 1) return `${Math.round(hours * 60)} 分钟`
  if (hours === 1) return '1 小时'
  if (Number.isInteger(hours)) return `${hours} 小时`
  return `${hours} 小时`
}

export function formatDifficulty(difficulty: Difficulty): string {
  const map: Record<Difficulty, string> = {
    beginner: '入门',
    intermediate: '中级',
    advanced: '高级',
  }
  return map[difficulty]
}

export function formatDifficultyColor(difficulty: Difficulty): string {
  const map: Record<Difficulty, string> = {
    beginner: 'var(--color-success)',
    intermediate: 'var(--color-warning)',
    advanced: 'var(--color-error)',
  }
  return map[difficulty]
}

export function formatDate(iso: string): string {
  const d = new Date(iso)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
