<template>
      <div class="container">

        <h3 class="header">Certificate Chain Extractor</h3>
        <span class="header-subtitle">This website allows you to extract and download the full certificate chain for a specified URL.<br>
        This can be useful when you need to import the full chain into a keystore, which is then used to verify the integrity of a website (often used in business applications).</span>
        <div class="row input-row">
          
          <div class="input-field ">
            <input type="url" id="url" class="col s12 m6" v-model="queryUrl" placeholder="https://www.google.de"/>
            <label for="url">Url</label>
          </div>
        </div>
          <button class="waves-effect waves-light btn-small blue accent-4" type="submit" name="action" v-on:click="onUrlSubmit">Submit
            <i class="material-icons right">send</i>
          </button>
        
        <ul v-if="certificates !== undefined" class="collection with-header" v-bind:class="{ fadeIn: certificates != undefined }">
          <li class="collection-header">Certificate chain</li>
          <li v-for="certificate in certificates" :key="certificate.fingerprint" class="collection-item avatar">
            <i class="material-icons circle green">lock</i>
            <span class="title bold">{{certificate.subject}}</span>
            <p>{{certificate.fingerprint}}</p>
            <p v-if="certificate.rootCertificate" class="medium">Certificate Authority</p>
            <a href="#!" v-on:click="downloadCertificate(certificate)" class="secondary-content"><i class="material-icons dark-grey">file_download</i></a>
          </li>
        </ul>
        <div v-if="loading" class="center-align loader"/>

      </div>
</template>

<script>
import backend from "./backend-api";
import materialize from 'materialize-css';

export default {
  data()  {
    return {
      queryUrl: '',
      certificates: undefined,
      loading: false
    }
  },
  methods: {
    onUrlSubmit: function() {
      this.certificates = undefined
      this.loading = true;

      let $this = this;
      backend.axios().get("/getChain?queryUrl=" + this.queryUrl)
      .then(function(response) {
        $this.certificates = response.data;

      }).catch(function(error) {
        // TODO show error  
      }).then(function() {
        $this.loading = false;
      });
    },
    downloadCertificate: function(certificate) {
      var element = document.createElement('a');
      element.setAttribute('href', 'data:text/octet-stream;charset=utf-8,' + encodeURIComponent(certificate.encodedContent));
      element.setAttribute('download', certificate.name + ".cer");

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

.header {
  font-weight: 700
}

.header-subtitle {
  font-size: 1.25rem;
}

.input-row {
  margin-top: 48px;
}

.loader {
  border: 9px solid #f3f3f3; 
  border-top: 9px solid #212121; 
  border-radius: 50%;
  margin-left: auto;
  margin-right: auto;
  margin-top: 240px;
  width: 72px;
  height: 72px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.fadeIn {
  animation: fadeIn ease 0.3s;
}

@keyframes fadeIn {
  0% {opacity:0;}
  100% {opacity:1;}
}
</style>
