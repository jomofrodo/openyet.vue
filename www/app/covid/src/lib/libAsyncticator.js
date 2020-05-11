import asyncTicator from './asyncTicator.vue'
import Vue from 'vue';

const libAsyncticator = {
    asyncIndicator,
    setupAxiosIndicators
};

export default libAsyncticator;

let asyncticator;

function asyncIndicator(flg, mountPoint) {
    // console.log("asyncIndicator: " + flg);
    if (!mountPoint) mountPoint = "asyncticator-mount";

    if (!asyncticator) {
        let div = document.getElementById(mountPoint);
        if (!div) {
            div = document.createElement('div', { id: mountPoint });
            document.body.append(div);
        }
        let ctor = Vue.extend(asyncTicator);  //make a component
        asyncticator = new ctor({
            propsData: { flgVisible: false }
        });
        asyncticator.$mount("#" + mountPoint);
    }
    asyncticator.flgVisible = flg;
    // Or, with jQuery:
    // if (flg) $('#ajaxAction').fadeIn();
    // else $('#ajaxAction').fadeOut();
}
function setupAxiosIndicators(instance, mountPoint) {
    instance.interceptors.request.use(
        config => {
            // trigger 'loading=true' event here
            asyncIndicator(true);
            return config;
        },
        error => {
            // trigger 'loading=false' event here
            asyncIndicator(false);
            return Promise.reject(error);
        }
    );

    instance.interceptors.response.use(
        response => {
            // trigger 'loading=false' event here
            asyncIndicator(false);
            return response;
        },
        error => {
            // trigger 'loading=false' event here
            asyncIndicator(false);
            return Promise.reject(error);
        }
    );
}
