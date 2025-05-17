<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const activeIntersection = ref<number | null>(null)
const trafficLights = ref<any[]>([])
const isActive = ref<boolean>(false)
let pollInterval: number | undefined = undefined

const createIntersection = async () => {
  try {
    const response = await axios.post('http://localhost:8080/intersections', {
      activeLight: 0
    })

    activeIntersection.value = response.data.id
    console.log('Created intersection with ID:', response.data.id)

    fetchLights()
  } catch (err) {
    console.error('Failed to create intersection', err)
  }
}

const activateLights = async () => {
  if (activeIntersection.value === null) return

  try {
    await axios.post(`http://localhost:8080/intersections/${activeIntersection.value}/activate`)
    isActive.value = true
    startPolling()
    console.log('Sending activation request...')
  } catch (err) {
    console.error(err)
  }
}

const deactivateLights = () => {
  isActive.value = false
  clearInterval(pollInterval)
  trafficLights.value = trafficLights.value.map(light => ({
    ...light, color: 'OFF'
  }))
  console.log('Lights deactivated')
}

const fetchLights = async () => {
  if (activeIntersection.value === null) return

  try {
    const res = await axios.get(`http://localhost:8080/intersections/${activeIntersection.value}`)
    if (!res.data.trafficLights || res.data.trafficLights.length === 0) {
      trafficLights.value = [
        { id: 1, color: 'OFF', road: 'Default', direction: 'NORTH' },
        { id: 2, color: 'OFF', road: 'Default', direction: 'SOUTH' },
        { id: 3, color: 'OFF', road: 'Default', direction: 'EAST' },
        { id: 4, color: 'OFF', road: 'Default', direction: 'WEST' }
      ]
    }
    else {
      trafficLights.value = res.data.trafficLights
    }
  } catch (err) {
    console.error('Failed to fetch lights', err)
  }
}

const startPolling = () => {
  fetchLights()
  pollInterval = setInterval(fetchLights, 1000)
}

onMounted(() => {
  createIntersection()
})
</script>

<template>
  <header>
    <div class="wrapper">
      <h1>Intersection Light Controller</h1>
    </div>
  </header>

  <main>
  <div class="light-controller">
    <div class="controls">
      <button @click="activateLights">Activate Lights</button>
      <button @click="deactivateLights">Deactivate Lights</button>
    </div>

    <div class="light-grid">
      <div class="light-box" v-for="light in trafficLights" :key="light.id">
        <h3>{{ light.road }} - {{ light.direction }}</h3>
        <div class="bulb" :class="light.color.toLowerCase()"></div>
        <p>Current: {{ light.color }}</p>
      </div>
    </div>

    <p>Status: {{ isActive ? 'Active' : 'Inactive' }}</p>
  </div>
</main>
</template>

<style scoped>
header {
  line-height: 1.5;
}

.light-controller {
  display: grid;
  place-items: center;
  gap: 1.5rem;
}

.controls {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.light-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.light-box {
  text-align: center;
}

.bulb {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: 0.5rem auto;
  border: 2px solid #333;
  background-color: #ccc;
}

.bulb.off {
  background-color: #ccc;
}

.bulb.red {
  background-color: #cc3232;
}

.bulb.yellow {
  background-color: #e7b416;
}

.bulb.green {
  background-color: #2dc937;
}

</style>
