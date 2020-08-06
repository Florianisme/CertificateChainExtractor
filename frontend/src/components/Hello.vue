<template>
      <div class="container">
        <h3>Certificate chain extractor</h3>
        <h4>Please input the url below:</h4>
        <div class="row">
          <input type="url" class="col s12 m6" v-model="queryUrl" placeholder="https://www.google.de"/>
          <button class="waves-effect waves-light btn-small grey darken-4" type="submit" name="action" v-on:click="onUrlSubmit">Submit
            <i class="material-icons right">send</i>
          </button>
        </div>
        <ul class="collection with-header">
          <li class="collection-header">Certificate chain</li>
          <li v-for="certificate in certificates" :key="certificate.fingerprint" class="collection-item avatar">
            <i class="material-icons circle green">lock</i>
            <span class="title bold">{{certificate.subject}}</span>
            <p>{{certificate.fingerprint}}</p>
            <p v-if="certificate.rootCertificate" class="medium">Certificate Authority</p>
            <a href="#!" v-on:click="downloadCertificate(certificate)" class="secondary-content"><i class="material-icons dark-grey">file_download</i></a>
          </li>
        </ul>
      </div>
</template>

<script>
import backend from "./backend-api";
import materialize from 'materialize-css';

export default {
  data()  {
    return {
      queryUrl: '',
      certificates: undefined
    }
  },
  methods: {
    onUrlSubmit: function() {
      this.certificates = [];
      backend.axios().get("/getChain?queryUrl=" + this.queryUrl)
      .then(function(response) {
        this.certificates = response.data;
      }.bind(this))
    },
    downloadCertificate: function(certificate) {
      var element = document.createElement('a');
      element.setAttribute('href', 'data:text/octet-stream;charset=utf-8,' + encodeURIComponent(certificate.encodedContent));
      element.setAttribute('download', certificate.fingerprint + ".cer");

      element.style.display = 'none';
      document.body.appendChild(element);

      element.click();

      document.body.removeChild(element);
    }
  }, 
  mounted() {
    materialize.AutoInit();
  }
}

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
span.bold {
  font-weight: 700;
}
p.medium {
  font-weight: 500;
}
i.dark-grey {
  color: #212121;
}
</style>
