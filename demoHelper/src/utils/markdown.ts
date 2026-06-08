import type { GenerateResponse } from '../types'
import { formatHours, formatDifficulty } from './formatters'

export function generateMarkdown(response: GenerateResponse): string {
  const lines: string[] = []

  lines.push(`# ${response.title}`)
  lines.push('')
  lines.push(`> ${response.description}`)
  lines.push('')
  lines.push('## 项目概览')
  lines.push('')
  lines.push(`| 指标 | 值 |`)
  lines.push(`|------|----|`)
  lines.push(`| 章节数 | ${response.metadata.totalSections} |`)
  lines.push(`| 预估总时长 | ${formatHours(response.metadata.estimatedTotalHours)} |`)
  lines.push(`| 难度 | ${formatDifficulty(response.metadata.difficulty)} |`)
  lines.push(`| 生成时间 | ${response.metadata.generatedAt} |`)
  lines.push('')

  lines.push('---')
  lines.push('')
  lines.push('## 完整提示词（可全部复制使用）')
  lines.push('')

  for (const section of response.sections) {
    lines.push(`### ${section.order}. ${section.title}`)
    lines.push('')
    lines.push(`> ${section.description}`)
    lines.push('')
    lines.push(`预估时间: ${formatHours(section.estimatedHours)} | 难度: ${formatDifficulty(section.difficulty)}`)
    lines.push('')
    lines.push('```')
    lines.push(section.prompt)
    lines.push('```')
    lines.push('')
    lines.push('---')
    lines.push('')
  }

  return lines.join('\n')
}
