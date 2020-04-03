(window["webpackJsonp"] = window["webpackJsonp"] || []).push([[3],{

/***/ "./node_modules/cache-loader/dist/cjs.js?!./node_modules/babel-loader/lib/index.js!./node_modules/cache-loader/dist/cjs.js?!./node_modules/vue-loader/lib/index.js?!./src/views/Home.vue?vue&type=script&lang=js&":
/*!********************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/cache-loader/dist/cjs.js??ref--12-0!./node_modules/babel-loader/lib!./node_modules/cache-loader/dist/cjs.js??ref--0-0!./node_modules/vue-loader/lib??vue-loader-options!./src/views/Home.vue?vue&type=script&lang=js& ***!
  \********************************************************************************************************************************************************************************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var vuex__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! vuex */ "./node_modules/vuex/dist/vuex.esm.js");
/* harmony import */ var _store_actions_type__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @/store/actions.type */ "./src/store/actions.type.js");
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//


/* harmony default export */ __webpack_exports__["default"] = ({
  name: "home",
  components: {},
  mounted: function mounted() {
    this.$store.dispatch(_store_actions_type__WEBPACK_IMPORTED_MODULE_1__["FETCH_TAGS"]);
  },
  computed: {
    // ...mapGetters(["isAuthenticated", "tags"]),
    isAuthenticated: function isAuthenticated() {
      return false;
    },
    tag: function tag() {
      return this.$route.params.tag;
    }
  }
});

/***/ }),

/***/ "./node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"node_modules/.cache/vue-loader\",\"cacheIdentifier\":\"3f5233b8-vue-loader-template\"}!./node_modules/vue-loader/lib/loaders/templateLoader.js?!./node_modules/cache-loader/dist/cjs.js?!./node_modules/vue-loader/lib/index.js?!./src/views/Home.vue?vue&type=template&id=fae5bece&":
/*!****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"3f5233b8-vue-loader-template"}!./node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ref--0-0!./node_modules/vue-loader/lib??vue-loader-options!./src/views/Home.vue?vue&type=template&id=fae5bece& ***!
  \****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
var render = function() {
  var _vm = this
  var _h = _vm.$createElement
  var _c = _vm._self._c || _h
  return _c("div", { staticClass: "home-page" }, [
    _vm._m(0),
    _c("div", { staticClass: "container page" }, [
      _c("div", { staticClass: "row" }, [
        _c(
          "div",
          { staticClass: "col-md-9" },
          [
            _c("div", { staticClass: "feed-toggle" }, [
              _c("ul", { staticClass: "nav nav-pills outline-active" }, [
                _c(
                  "li",
                  { staticClass: "nav-item" },
                  [
                    _c(
                      "router-link",
                      {
                        staticClass: "nav-link",
                        attrs: {
                          to: { name: "nations-static" },
                          exact: "",
                          "active-class": "active"
                        }
                      },
                      [_vm._v(" Static Feed ")]
                    )
                  ],
                  1
                ),
                _c(
                  "li",
                  { staticClass: "nav-item" },
                  [
                    _c(
                      "router-link",
                      {
                        staticClass: "nav-link",
                        attrs: {
                          to: { name: "nations" },
                          exact: "",
                          "active-class": "active"
                        }
                      },
                      [_vm._v(" Dynamic Feed ")]
                    )
                  ],
                  1
                )
              ])
            ]),
            _c("router-view")
          ],
          1
        )
      ])
    ])
  ])
}
var staticRenderFns = [
  function() {
    var _vm = this
    var _h = _vm.$createElement
    var _c = _vm._self._c || _h
    return _c("div", { staticClass: "banner" }, [
      _c("div", { staticClass: "container" }, [
        _c("h1", { staticClass: "logo-font" }, [_vm._v("Covid 19")]),
        _c("p", [_vm._v("Analysis of covid19 data")])
      ])
    ])
  }
]
render._withStripped = true



/***/ }),

/***/ "./src/store/actions.type.js":
/*!***********************************!*\
  !*** ./src/store/actions.type.js ***!
  \***********************************/
/*! exports provided: ARTICLE_PUBLISH, ARTICLE_DELETE, ARTICLE_EDIT, ARTICLE_EDIT_ADD_TAG, ARTICLE_EDIT_REMOVE_TAG, ARTICLE_RESET_STATE, CHECK_AUTH, COMMENT_CREATE, COMMENT_DESTROY, FAVORITE_ADD, FAVORITE_REMOVE, FETCH_ARTICLE, FETCH_ARTICLES, FETCH_COMMENTS, FETCH_PROFILE, FETCH_PROFILE_FOLLOW, FETCH_PROFILE_UNFOLLOW, FETCH_TAGS, LOGIN, LOGOUT, REGISTER, UPDATE_USER */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTICLE_PUBLISH", function() { return ARTICLE_PUBLISH; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTICLE_DELETE", function() { return ARTICLE_DELETE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTICLE_EDIT", function() { return ARTICLE_EDIT; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTICLE_EDIT_ADD_TAG", function() { return ARTICLE_EDIT_ADD_TAG; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTICLE_EDIT_REMOVE_TAG", function() { return ARTICLE_EDIT_REMOVE_TAG; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTICLE_RESET_STATE", function() { return ARTICLE_RESET_STATE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CHECK_AUTH", function() { return CHECK_AUTH; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "COMMENT_CREATE", function() { return COMMENT_CREATE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "COMMENT_DESTROY", function() { return COMMENT_DESTROY; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FAVORITE_ADD", function() { return FAVORITE_ADD; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FAVORITE_REMOVE", function() { return FAVORITE_REMOVE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_ARTICLE", function() { return FETCH_ARTICLE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_ARTICLES", function() { return FETCH_ARTICLES; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_COMMENTS", function() { return FETCH_COMMENTS; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_PROFILE", function() { return FETCH_PROFILE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_PROFILE_FOLLOW", function() { return FETCH_PROFILE_FOLLOW; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_PROFILE_UNFOLLOW", function() { return FETCH_PROFILE_UNFOLLOW; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FETCH_TAGS", function() { return FETCH_TAGS; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LOGIN", function() { return LOGIN; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LOGOUT", function() { return LOGOUT; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "REGISTER", function() { return REGISTER; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UPDATE_USER", function() { return UPDATE_USER; });
var ARTICLE_PUBLISH = "publishArticle";
var ARTICLE_DELETE = "deleteArticle";
var ARTICLE_EDIT = "editArticle";
var ARTICLE_EDIT_ADD_TAG = "addTagToArticle";
var ARTICLE_EDIT_REMOVE_TAG = "removeTagFromArticle";
var ARTICLE_RESET_STATE = "resetArticleState";
var CHECK_AUTH = "checkAuth";
var COMMENT_CREATE = "createComment";
var COMMENT_DESTROY = "destroyComment";
var FAVORITE_ADD = "addFavorite";
var FAVORITE_REMOVE = "removeFavorite";
var FETCH_ARTICLE = "fetchArticle";
var FETCH_ARTICLES = "fetchArticles";
var FETCH_COMMENTS = "fetchComments";
var FETCH_PROFILE = "fetchProfile";
var FETCH_PROFILE_FOLLOW = "fetchProfileFollow";
var FETCH_PROFILE_UNFOLLOW = "fetchProfileUnfollow";
var FETCH_TAGS = "fetchTags";
var LOGIN = "login";
var LOGOUT = "logout";
var REGISTER = "register";
var UPDATE_USER = "updateUser";

/***/ }),

/***/ "./src/views/Home.vue":
/*!****************************!*\
  !*** ./src/views/Home.vue ***!
  \****************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _Home_vue_vue_type_template_id_fae5bece___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Home.vue?vue&type=template&id=fae5bece& */ "./src/views/Home.vue?vue&type=template&id=fae5bece&");
/* harmony import */ var _Home_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Home.vue?vue&type=script&lang=js& */ "./src/views/Home.vue?vue&type=script&lang=js&");
/* empty/unused harmony star reexport *//* harmony import */ var _node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../node_modules/vue-loader/lib/runtime/componentNormalizer.js */ "./node_modules/vue-loader/lib/runtime/componentNormalizer.js");





/* normalize component */

var component = Object(_node_modules_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_2__["default"])(
  _Home_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__["default"],
  _Home_vue_vue_type_template_id_fae5bece___WEBPACK_IMPORTED_MODULE_0__["render"],
  _Home_vue_vue_type_template_id_fae5bece___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  null,
  null
  
)

/* hot reload */
if (false) { var api; }
component.options.__file = "src/views/Home.vue"
/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ "./src/views/Home.vue?vue&type=script&lang=js&":
/*!*****************************************************!*\
  !*** ./src/views/Home.vue?vue&type=script&lang=js& ***!
  \*****************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _node_modules_cache_loader_dist_cjs_js_ref_12_0_node_modules_babel_loader_lib_index_js_node_modules_cache_loader_dist_cjs_js_ref_0_0_node_modules_vue_loader_lib_index_js_vue_loader_options_Home_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../node_modules/cache-loader/dist/cjs.js??ref--12-0!../../node_modules/babel-loader/lib!../../node_modules/cache-loader/dist/cjs.js??ref--0-0!../../node_modules/vue-loader/lib??vue-loader-options!./Home.vue?vue&type=script&lang=js& */ "./node_modules/cache-loader/dist/cjs.js?!./node_modules/babel-loader/lib/index.js!./node_modules/cache-loader/dist/cjs.js?!./node_modules/vue-loader/lib/index.js?!./src/views/Home.vue?vue&type=script&lang=js&");
/* empty/unused harmony star reexport */ /* harmony default export */ __webpack_exports__["default"] = (_node_modules_cache_loader_dist_cjs_js_ref_12_0_node_modules_babel_loader_lib_index_js_node_modules_cache_loader_dist_cjs_js_ref_0_0_node_modules_vue_loader_lib_index_js_vue_loader_options_Home_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__["default"]); 

/***/ }),

/***/ "./src/views/Home.vue?vue&type=template&id=fae5bece&":
/*!***********************************************************!*\
  !*** ./src/views/Home.vue?vue&type=template&id=fae5bece& ***!
  \***********************************************************/
/*! exports provided: render, staticRenderFns */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _node_modules_cache_loader_dist_cjs_js_cacheDirectory_node_modules_cache_vue_loader_cacheIdentifier_3f5233b8_vue_loader_template_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_node_modules_cache_loader_dist_cjs_js_ref_0_0_node_modules_vue_loader_lib_index_js_vue_loader_options_Home_vue_vue_type_template_id_fae5bece___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!../../node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"3f5233b8-vue-loader-template"}!../../node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!../../node_modules/cache-loader/dist/cjs.js??ref--0-0!../../node_modules/vue-loader/lib??vue-loader-options!./Home.vue?vue&type=template&id=fae5bece& */ "./node_modules/cache-loader/dist/cjs.js?{\"cacheDirectory\":\"node_modules/.cache/vue-loader\",\"cacheIdentifier\":\"3f5233b8-vue-loader-template\"}!./node_modules/vue-loader/lib/loaders/templateLoader.js?!./node_modules/cache-loader/dist/cjs.js?!./node_modules/vue-loader/lib/index.js?!./src/views/Home.vue?vue&type=template&id=fae5bece&");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _node_modules_cache_loader_dist_cjs_js_cacheDirectory_node_modules_cache_vue_loader_cacheIdentifier_3f5233b8_vue_loader_template_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_node_modules_cache_loader_dist_cjs_js_ref_0_0_node_modules_vue_loader_lib_index_js_vue_loader_options_Home_vue_vue_type_template_id_fae5bece___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _node_modules_cache_loader_dist_cjs_js_cacheDirectory_node_modules_cache_vue_loader_cacheIdentifier_3f5233b8_vue_loader_template_node_modules_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_node_modules_cache_loader_dist_cjs_js_ref_0_0_node_modules_vue_loader_lib_index_js_vue_loader_options_Home_vue_vue_type_template_id_fae5bece___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });



/***/ })

}]);
//# sourceMappingURL=3.js.map