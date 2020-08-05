<template>
  <div class="hello">
    <h1>Certificate chain extractor</h1>
    <h2>Please input the url below:</h2>
    <input type="url" v-model="queryUrl" placeholder="https://www.google.de"/>
    <button v-on:click="onUrlSubmit">Submit</button>

    <ul>
      <li v-for="certificate in certificates" :key="certificate.fingerprint">
        {{certificate.subject}}
      </li>
    </ul>
  </div>
</template>

<script>
import backend from "./backend-api";

export default {
  name: 'hello',
  props: { hellomsg: { type: String, required: true } },
  data: {
    queryUrl: '',
    certificates: [
      {
        subject: undefined,
        fingerprint: undefined,
        encodedContent: undefined,
        isRootCertificate: undefined, 
        isValid: undefined
      }
    ]
  },
  methods: {
    onUrlSubmit: function() {
      backend.axios().get("/getChain?queryUrl=" + this.queryUrl)
      .then(function(response) {
        this.certificates.push(JSON.parse(response));
      })
    }
  }
}

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
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
</style>
