<template>
  <div id="app">
    <h1>GitHub Repository Card<br>for every web site</h1>
    <a href="https://github.com/nwtgck/gh-card" target="_blank">
      <img src="./assets/github.svg" style="width: 1em;"> GitHub
    </a>
    <GhCardGenerator />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import GhCardGenerator from './components/GhCardGenerator.vue';

@Component({
  components: {
    GhCardGenerator,
  },
})
export default class App extends Vue {}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 50px;
}
a {
  color: black;
}
</style>
