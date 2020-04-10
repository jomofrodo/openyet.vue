import { Typeahead } from 'uiv';
//import { Typeahead } from '../../3rdparty/uiv/dist/uiv.min';

export default {
    name: "ds-typeahead",
    extends: Typeahead,
    watch: {
      /*
     data () {
        this.prepareItems(this.data)
        this.open = Boolean(this.items.length && this.value)
      },
      */
    },

    methods:{
      fetchItemsXX (value, debounce) {
        clearTimeout(this.timeoutID)
        if (value === '' && !this.openOnEmpty) {
          this.open = false
        } else if (this.data) {
          this.prepareItems(this.data)
          this.open = Boolean(this.items.length)
        } else if (this.asyncSrc) {
          this.timeoutID = setTimeout(() => {
            this.$emit('loading');
            const vm = this;
            this.$http.get(this.asyncSrc + value)
              .then(data => {
                if (vm.inputEl.matches(':focus')) {
                  vm.prepareItems(vm.asyncKey ? data[vm.asyncKey] : data, true)
                  vm.open = Boolean(vm.items.length)
                }
                vm.$emit('loaded')
              })
              .catch(err => {
                console.error(err)
                this.$emit('loaded-error')
              })
          }, debounce)
        }
      },
      prepareItemsXX (data, disableFilters = false) {
          if (disableFilters) {
            this.items = data.slice(0, this.limit)
            return
          }
          this.items = []
          this.activeIndex = this.preselect ? 0 : -1
          for (let i = 0, l = data.length; i < l; i++) {
            let item = data[i]
            let key = this.itemKey ? item[this.itemKey] : item
            key = key.toString()
            let index = -1
            if (this.ignoreCase) {
              index = key.toLowerCase().indexOf(this.inputEl.value.toLowerCase())
            } else {
              index = key.indexOf(this.inputEl.value)
            }
            if (this.matchStart ? index === 0 : index >= 0) {
              this.items.push(item)
            }
            if (this.items.length >= this.limit) {
              break
            }
          }
       },
       selectItem (item) {
        // item comes in as {label:val,value:val}

        //console.log("marsha: " + item.value);
        this.$emit('input', item.value)
        this.open = false
      },
    }
  }
