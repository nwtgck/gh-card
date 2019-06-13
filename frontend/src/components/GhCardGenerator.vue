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
      <input type="text" v-model="repoName" placeholder="user/repo" class="repo_name">
      <button @click="update()">Generate</button><br>
    </p>
    <div v-if="imageGenerated">
      <p>
        <input type="radio" v-model="imageExtension" value="svg">SVG
        <input type="radio" v-model="imageExtension" value="png">PNG
      </p>

      <a :href="gitHubRepoUrl">
        <img :src="imageUrl">
      </a><br>
      <h3>Image URL</h3>
      <input type="text" :value="imageUrl" size="60">
      <h3>HTML</h3>
      <textarea cols="60" rows="3">{{ embedHtml }}</textarea>
      <h3>Markdown</h3>
      <textarea cols="60" rows="3">{{ embedMarkdown }}</textarea>
      <h3><a href="https://scrapbox.io" target="_blank">Scrapbox</a></h3>
      <textarea cols="60" rows="3">{{ embedScrapbox }}</textarea>
    </div>
  </div>
</template>

<script lang="ts">
/* tslint:disable:no-console */
import { Component, Watch, Vue } from 'vue-property-decorator';
import consts from '@/constants';

console.log(`image server is ${consts.imageServerUrl}`);

@Component
export default class GhCardGenerator extends Vue {
  private exampleRepoNames = [
    'mozilla/send',
  ];

  private repoName = '';
  private imageExtension = 'svg';

  private imageGenerated: boolean = false;
  private gitHubRepoUrl = '';
  private imageUrl = '';
  private embedHtml     = '';
  private embedMarkdown = '';
  private embedScrapbox = '';

  private mounted() {
    window.addEventListener('keydown', (e: WindowEventMap['keydown']) => {
      // Meta + Enter
      // (Ctrl + Enter or Cmd + Enter)
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        Vue.nextTick(() => {
          this.update();
        });
      }
    });
  }

  private update() {
    this.gitHubRepoUrl = this.getGitHubRepoUrl(this.repoName);
    this.imageUrl      = this.getImgUrl(this.repoName, this.imageExtension);
    this.embedHtml     = this.getEmbedHtml(this.repoName, this.imageExtension);
    this.embedMarkdown = this.getEmbedMarkdown(this.repoName, this.imageExtension);
    this.embedScrapbox = this.getEmbedScrapbox(this.repoName, this.imageExtension);
    // NOTE: This will be never false
    this.imageGenerated = true;
  }

  private getImgUrl(repoName: string, imageExtension: string): string {
    return `${consts.imageServerUrl}/repos/${repoName}.${imageExtension}`;
  }

  private getGitHubRepoUrl(repoName: string): string {
    return `https://github.com/${repoName}`;
  }

  private getEmbedHtml(repoName: string, imageExtension: string): string {
    return `<a href="${this.getGitHubRepoUrl(repoName)}"><img src="${this.getImgUrl(repoName, imageExtension)}"></a>`;
  }

  private getEmbedMarkdown(repoName: string, imageExtension: string): string {
    return `[![${repoName} - GitHub](${this.getImgUrl(repoName, imageExtension)})](${this.getGitHubRepoUrl(repoName)})`;
  }

  private getEmbedScrapbox(repoName: string, imageExtension: string): string {
    return `[${this.getImgUrl(repoName, imageExtension)} ${this.getGitHubRepoUrl(repoName)}]`;
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
  margin: 1em 0 0;
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
.repo_name {
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
