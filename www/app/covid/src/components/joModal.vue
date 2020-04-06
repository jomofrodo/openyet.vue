<!-- template for the modal component -->
<template id="modal-template">
  <transition name="fade">
    <div class="modal-mask" v-if="flgDlg">
      <div class="modal-wrapper">
        <div class="modal-container">

          <div class="modal-header-jm">
            <h3><slot name="header"></slot></h3>
          </div>

          <div class="modal-body-jm">         
            <slot>
              <div v-html="dlgMsg"></div>
            </slot>
          </div>

          <div class="modal-footer">
            <slot name="footer">
              <div>
              <button class="btn btn-sm btn-primary" v-if="flgCheck(flgOK)" @click.stop.prevent="handleOKClick">OK</button>
              <button class="btn btn-sm btn-danger" v-if="flgCheck(flgCancel)" @click.stop.prevent="handleCancelClick">Cancel</button>
              </div>
            </slot>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>


<script>
import Vue from "vue";

export default {
  name: "jo-modal",
  data: function() {
    return {
      id: this.dlgID || null,
      flgDlg: this.flgShow,
      flgDebug: true
    };
  },

  props: [
    "dlgName",
    "flgOK",
    "flgCancel",
    "pClickOK",
    "pClickCancel",
    "dlgMsg",
    "dlgHeader",
    "dlgID",
    "flgShow"
  ],
  watch: {
    flgShow(newVal) {
      this.flgDlg = newVal;
    }
  },
  beforeCreate: function() {
    // nada
  },
  created: function() {},
  mounted: function() {},
  computed: {
    dlgCode: function() {
      let code = this.dlgName;
      //if(this.dlgID) code += "_" + this.dlgID;
      return code;
    }
  },
  methods: {
    flgCheck: function(flg) {
      //flags are true by default
      return !(flg === "false");
    },
    getDialogFlag: function() {},
    handleCancelClick: function() {
      if (this.pClickCancel) this.pClickCancel();
      this.$emit("cancel");
      this._hideModal();
    },
    handleOKClick: function() {
      if (this.pClickOK) this.pClickOK();
      this.$emit("close");
      this.$emit("ok");
      this._hideModal();
    },
    _hideModal: function() {
      this.flgDlg = false;
    },
    _showModal: function() {
      this.flgDlg = true;
    }
  }
};
</script>


<style type="text/css">
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: table;
  transition: opacity 0.3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.modal-container {
  width: auto;
  /*display:inline-block;*/
  max-width: 600px;
  margin: 0px auto;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  font-family: Helvetica, Arial, sans-serif;
}

.modal-header-jm h3 {
  margin-top: 0;
  /* color: #42b983;*/
}

.modal-body-jm {
  margin: 20px 0;
  max-height: 70vh;
  overflow: auto;
}

/*
 * The following styles are auto-applied to elements with
 * transition="modal" when their visibility is toggled
 * by Vue.js.
 *
 * You can easily play with the modal transition by editing
 * these styles.
 */

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
</style>
