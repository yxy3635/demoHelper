import type { GenerateResponse } from '../types'
import { generateMarkdown } from '../utils/markdown'

export function useExport() {
  function getMarkdown(result: GenerateResponse): string {
    return generateMarkdown(result)
  }

  function getJson(result: GenerateResponse): string {
    return JSON.stringify(result, null, 2)
  }

  function getFullPrompt(result: GenerateResponse): string {
    const parts: string[] = []
    parts.push(`# ${result.title}`)
    parts.push('')
    parts.push(result.description)
    parts.push('')

    for (const section of result.sections) {
      parts.push(`## ${section.order}. ${section.title}`)
      parts.push('')
      parts.push(section.prompt)
      parts.push('')
    }

    return parts.join('\n')
  }

  async function copyToClipboard(text: string): Promise<'idle' | 'copied' | 'error'> {
    try {
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(text)
        return 'copied'
      }
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.style.cssText = 'position:fixed;opacity:0'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      return 'copied'
    } catch {
      return 'error'
    }
  }

  function downloadFile(content: string, filename: string, mimeType: string) {
    const blob = new Blob([content], { type: mimeType })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  }

  function downloadMarkdown(result: GenerateResponse) {
    const md = getMarkdown(result)
    downloadFile(md, `${sanitize(result.title)}.md`, 'text/markdown;charset=utf-8')
  }

  function downloadJson(result: GenerateResponse) {
    const json = getJson(result)
    downloadFile(json, `${sanitize(result.title)}.json`, 'application/json;charset=utf-8')
  }

  function sanitize(name: string): string {
    return name.replace(/[^a-zA-Z0-9一-鿿_-]/g, '_').slice(0, 80) || 'output'
  }

  return { getMarkdown, getJson, getFullPrompt, copyToClipboard, downloadMarkdown, downloadJson }
}
