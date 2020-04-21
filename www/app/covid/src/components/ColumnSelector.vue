<template>
    <div>
        <div >
            <i class="fa-check-square" :class="{'fas':flgDisplay,'far':!flgDisplay}" @click="toggleDisplay" title="Column Selector" />
        </div>
        <div class="small-modal" v-if="flgDisplay">
            Column Selector:
            <ul>
                <li v-for="col in colDefs" :key="col.colName">{{col.header}} <input type="checkbox" v-model="col.isVisible" @click="toggleHidden(col)"/></li>
            </ul>
        </div>
    </div>
</template>
<script>
export default {
    name: "ColumnSelector",
    props: { colDefs: Array
            },
    data: function(){
        return{
            flgDebug: true,
            flgDisplay: false
        }
    },
    created(){
        console.log(this.colDefs);
    },
    watch:{
        colDefs(newVal){
            console.log(newVal);
        }
    },
    methods:{
        isVisible(col){
            return !col.hidden;
        },
        toggleDisplay(){
            this.flgDisplay = !this.flgDisplay;
        },
        toggleHidden(col){
            col.hidden = !col.hidden;
        }
    }
}
</script>