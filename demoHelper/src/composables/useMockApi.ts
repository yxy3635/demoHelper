import type { GenerateRequest, GenerateResponse } from '../types'
import { sampleResponse } from '../mock/sampleResponse'
import { MOCK_DELAY_MS } from '../mock/delay'

let callCount = 0

export function useMockApi() {
  async function generate(_request: GenerateRequest): Promise<GenerateResponse> {
    // Simulate network delay
    await new Promise((resolve) => setTimeout(resolve, MOCK_DELAY_MS))

    // Optionally simulate a random failure (5% chance, useful for testing error states)
    // if (Math.random() < 0.05) {
    //   throw new Error('网络请求失败，请重试')
    // }

    callCount++

    // Return a deep clone so callers can't mutate the sample data
    const response: GenerateResponse = JSON.parse(JSON.stringify(sampleResponse))
    response.id = `gen-${Date.now()}-${callCount}`
    response.metadata.generatedAt = new Date().toISOString()

    return response
  }

  return { generate }
}
