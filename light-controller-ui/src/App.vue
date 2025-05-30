<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const isLoading = ref(false)
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
  if (activeIntersection.value === null || isLoading.value || isActive.value) 
  return isLoading.value = true

  try {
    await axios.post(`http://localhost:8080/intersections/${activeIntersection.value}/activate`)
    isActive.value = true
    startPolling()
    console.log('Activating lights...')
  } catch (err) {
    console.error(err)
  } finally {
    isLoading.value = false
  }
}

const deactivateLights = async () => {
  if (activeIntersection.value === null || isLoading.value || !isActive.value) 
  return isLoading.value = true

  isActive.value = false
  clearInterval(pollInterval)

  try {
    await axios.post(`http://localhost:8080/intersections/${activeIntersection.value}/deactivate`)
    isActive.value = false
    clearInterval(pollInterval)
    trafficLights.value = trafficLights.value.map(light => ({
      ...light, color: 'OFF'
    }))
    console.log('Lights deactivated')
  } catch (err) {
    console.error(err)
  } finally {
    isLoading.value = false
  }
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

const getLight = (direction: string) => {
  return trafficLights.value.find(light => light.direction.toLowerCase() === direction.toLowerCase())
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
        <button @click="activateLights" :disabled="isLoading || isActive">Activate Lights</button>
        <button @click="deactivateLights" :disabled="isLoading || !isActive">Deactivate Lights</button>
      </div>
      <p>Status: {{ isActive ? 'Active' : 'Inactive' }}</p>

      <div class="intersection-layout">
        <div class="light north" v-if="getLight('North')">
          <div class="light-box">
            <h3>{{ getLight('North').road }} - {{ getLight('North').direction }}</h3>
            <div class="light-stack">
              <div class="bulb red" :class="{ on: getLight('North').color === 'RED' }"></div>
              <div class="bulb yellow" :class="{ on: getLight('North').color === 'YELLOW' }"></div>
              <div class="bulb green" :class="{ on: getLight('North').color === 'GREEN' }"></div>
            </div>
          </div>
        </div>

        <div class="light west" v-if="getLight('West')">
          <div class="light-box">
            <h3>{{ getLight('West').road }} - {{ getLight('West').direction }}</h3>
            <div class="light-stack">
              <div class="bulb red" :class="{ on: getLight('West').color === 'RED' }"></div>
              <div class="bulb yellow" :class="{ on: getLight('West').color === 'YELLOW' }"></div>
              <div class="bulb green" :class="{ on: getLight('West').color === 'GREEN' }"></div>
            </div>
          </div>
        </div>

        <div class="center-label">
          <img src="./assets/intersection.png" alt="Intersection" />
        </div>

        <div class="light east" v-if="getLight('East')">
          <div class="light-box">
            <h3>{{ getLight('East').road }} - {{ getLight('East').direction }}</h3>
            <div class="light-stack">
              <div class="bulb red" :class="{ on: getLight('East').color === 'RED' }"></div>
              <div class="bulb yellow" :class="{ on: getLight('East').color === 'YELLOW' }"></div>
              <div class="bulb green" :class="{ on: getLight('East').color === 'GREEN' }"></div>
            </div>
          </div>
        </div>

        <div class="light south" v-if="getLight('South')">
          <div class="light-box">
            <h3>{{ getLight('South').road }} - {{ getLight('South').direction }}</h3>
            <div class="light-stack">
              <div class="bulb red" :class="{ on: getLight('South').color === 'RED' }"></div>
              <div class="bulb yellow" :class="{ on: getLight('South').color === 'YELLOW' }"></div>
              <div class="bulb green" :class="{ on: getLight('South').color === 'GREEN' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
  text-align: center;
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

.intersection-layout {
  display: grid;
  grid-template-areas:
    ".    north    ."
    "west center  east"
    ".    south    .";
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: auto auto auto;
  gap: 1rem;
  justify-items: center;
  align-items: center;
  margin-top: 2rem;
}

.north {
  grid-area: north;
}

.south {
  grid-area: south;
}

.east {
  grid-area: east;
}

.west {
  grid-area: west;
}

.center-label img {
  max-width: 130px;
  max-height: 130px;
  object-fit: contain;
}

.center-label {
  grid-area: center;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.5rem;
}

.light-box {
  text-align: center;
}

.light-stack {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  padding: 0.6rem 0.4rem;
  width: 60px;
  align-items: center;
  margin-top: 0.5rem;
  border: #e7b416 2px solid;
  border-radius: 8px;
  background-color: #222;
  box-shadow: 0 0 10px #e7b416;
  margin: 0 auto;
}

.bulb {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 2px solid #333;
  background-color: #ccc;
  transition: background-color 0.3s;
}

.bulb.on.red {
  background-color: #cc3232;
}

.bulb.on.yellow {
  background-color: #e7b416;
}

.bulb.on.green {
  background-color: #2dc937;
}
</style>
