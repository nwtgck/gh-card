<template>
  <div>
    <div>example:</div>
    <span v-for="exampleRepoName in exampleRepoNames">
      <a :href="getGitHubRepoUrl(exampleRepoName)">
      <img :src="getImgUrl(exampleRepoName, 'svg')">
    </a>
    </span>
    <hr>
    <p>
      <input type="text" v-model="repoName" placeholder="user/repo"><br>
      <button @click="update()">Generate</button><br>
    </p>
    <input type="radio" v-model="imageExtension" value="svg">SVG
    <input type="radio" v-model="imageExtension" value="png">PNG
    <div v-if="imageGenerated">
      <a :href="gitHubRepoUrl">
        <img :src="imageUrl">
      </a><br>
      <textarea cols="60" rows="5">{{ imageTag }}</textarea>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Watch, Vue } from 'vue-property-decorator';

@Component
export default class GhCardGenerator extends Vue {
  // TODO: Hard code
  private imageServerUrl: string = 'http://localhost:8080';

  private exampleRepoNames = [
    'mozilla/send',
  ];

  private repoName = '';
  private imageExtension = 'svg';

  private imageGenerated: boolean = false;
  private gitHubRepoUrl = '';
  private imageUrl = '';
  private imageTag = '';

  private update() {
    this.gitHubRepoUrl = this.getGitHubRepoUrl(this.repoName);
    this.imageUrl      = this.getImgUrl(this.repoName, this.imageExtension);
    this.imageTag      = this.getImageTag(this.repoName, this.imageExtension);
    // NOTE: This will be never false
    this.imageGenerated = true;
  }

  private getImgUrl(repoName: string, imageExtension: string): string {
    return `${this.imageServerUrl}/repos/${repoName}.${imageExtension}`;
  }

  private getGitHubRepoUrl(repoName: string): string {
    return `https://github.com/${repoName}`;
  }

  private getImageTag(repoName: string, imageExtension: string): string {
    return `<a href="${this.getGitHubRepoUrl(repoName)}"><img src="${this.getImgUrl(repoName, imageExtension)}"></a>`;
  }

  @Watch('imageExtension')
  private onImageExtension() {
    if (this.imageGenerated) {
      this.update();
    }
  }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
input[type='text'] {
  font-size: 2em;
  width: 15em;
  text-align: center;
}
button {
  margin: 1em 0;
  font-size: 2em;
}
img {
  width: 500px;
}
</style>
