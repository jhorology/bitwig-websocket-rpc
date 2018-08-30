webpackHotUpdate(5,{

/***/ "./components/Header.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("./node_modules/react/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_next_router__ = __webpack_require__("./node_modules/next/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_next_router___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_next_router__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prop_types__ = __webpack_require__("./node_modules/next/node_modules/prop-types/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prop_types___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_prop_types__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux__ = __webpack_require__("./node_modules/redux/es/redux.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__ = __webpack_require__("./node_modules/grommet/components/Box/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading__ = __webpack_require__("./node_modules/grommet/components/Heading/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_Select__ = __webpack_require__("./node_modules/grommet/components/Select/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_Select___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_grommet_components_Select__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer__ = __webpack_require__("./node_modules/grommet/components/Layer/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_grommet_components_Button__ = __webpack_require__("./node_modules/grommet/components/Button/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_grommet_components_Button___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_8_grommet_components_Button__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System__ = __webpack_require__("./node_modules/grommet-icons/icons/System.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu__ = __webpack_require__("./node_modules/grommet-icons/icons/Menu.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11_grommet_icons_icons_Connect__ = __webpack_require__("./node_modules/grommet-icons/icons/Connect.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11_grommet_icons_icons_Connect___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_11_grommet_icons_icons_Connect__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12__redux__ = __webpack_require__("./redux/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_13__RoutedButton__ = __webpack_require__("./components/RoutedButton.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_14__RoutedAnchor__ = __webpack_require__("./components/RoutedAnchor.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_15__Anchor__ = __webpack_require__("./components/Anchor.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_16__redux_themes_actions__ = __webpack_require__("./redux/themes/actions.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/Header.js";

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; var ownKeys = Object.keys(source); if (typeof Object.getOwnPropertySymbols === 'function') { ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) { return Object.getOwnPropertyDescriptor(source, sym).enumerable; })); } ownKeys.forEach(function (key) { _defineProperty(target, key, source[key]); }); } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }



















var Header =
/*#__PURE__*/
function (_React$Component) {
  _inherits(Header, _React$Component);

  function Header(props, context) {
    var _this;

    _classCallCheck(this, Header);

    _this = _possibleConstructorReturn(this, (Header.__proto__ || Object.getPrototypeOf(Header)).call(this, props, context));
    Object.defineProperty(_assertThisInitialized(_this), "state", {
      configurable: true,
      enumerable: true,
      writable: true,
      value: {
        activeMenu: false
      }
    });
    Object.defineProperty(_assertThisInitialized(_this), "onResponsiveMenu", {
      configurable: true,
      enumerable: true,
      writable: true,
      value: function value() {
        _this.setState({
          activeMenu: !_this.state.activeMenu
        });
      }
    });
    Object.defineProperty(_assertThisInitialized(_this), "onCloseMenu", {
      configurable: true,
      enumerable: true,
      writable: true,
      value: function value() {
        _this.setState({
          activeMenu: false
        });
      }
    });
    Object.defineProperty(_assertThisInitialized(_this), "onThemeChange", {
      configurable: true,
      enumerable: true,
      writable: true,
      value: function value(_ref) {
        var theme = _ref.option;
        var router = _this.props.router;
        var path = {
          pathname: router.pathname,
          query: _objectSpread({}, router.query, {
            theme: theme
          })
        };

        _this.changeTheme(theme);

        router.replace(path, path, {
          shallow: true
        });
      }
    });

    _this.changeTheme(props.router.query.theme);

    return _this;
  }

  _createClass(Header, [{
    key: "changeTheme",
    value: function changeTheme(themeName) {
      this.props.selectTheme(themeName);
      this.theme = themeName;
    }
  }, {
    key: "componentWillReceiveProps",
    value: function componentWillReceiveProps(nextProps) {
      if (nextProps.router.query.theme !== this.theme) {
        this.changeTheme(nextProps.router.query.theme);
      }
    }
  }, {
    key: "render",
    value: function render() {
      var _props = this.props,
          pageTitle = _props.title,
          _props$themes = _props.themes,
          themes = _props$themes.themes,
          theme = _props$themes.selected,
          size = _props.size;
      var isNarrow = size === 'narrow';
      var isWide = size === 'wide'; // const keywords = ['grommet', 'grommet 2', 'react', 'next-js', 'next.js', 'ui library'];
      // if (pageTitle) {
      //   keywords.push(pageTitle);
      // }

      var menuItems = [{
        path: '/get-started',
        label: 'get-started'
      }];
      var items = menuItems.map(function (item) {
        return item.external ? __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_15__Anchor__["a" /* default */], {
          target: "_blank",
          key: item.label,
          path: item.external,
          label: item.label,
          __source: {
            fileName: _jsxFileName,
            lineNumber: 61
          }
        }) : __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14__RoutedAnchor__["a" /* default */], {
          key: item.label,
          path: item.path,
          label: item.label,
          __source: {
            fileName: _jsxFileName,
            lineNumber: 63
          }
        });
      });
      var themeSelector = __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__["Box"], {
        basis: "small",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 67
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_6_grommet_components_Select__["Select"], {
        a11yTitle: "Change theme",
        value: theme,
        options: Object.keys(themes),
        onChange: this.onThemeChange,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 68
        }
      }));
      var themeDesigner = __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_15__Anchor__["a" /* default */], {
        icon: isNarrow ? undefined : __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System__["System"], {
          __source: {
            fileName: _jsxFileName,
            lineNumber: 78
          }
        }),
        label: isNarrow ? 'theme designer' : undefined,
        path: "/theme",
        a11yTitle: "theme designer",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 77
        }
      });
      var connectBitwigStudio = __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_15__Anchor__["a" /* default */], {
        icon: isNarrow ? undefined : __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_11_grommet_icons_icons_Connect__["Connect"], {
          __source: {
            fileName: _jsxFileName,
            lineNumber: 86
          }
        }),
        label: isNarrow ? 'connect Bittwig Studio' : undefined,
        path: "/connect",
        a11yTitle: "connect Bitwig Studio",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 85
        }
      });
      var menu;

      if (isNarrow) {
        if (this.state.activeMenu) {
          menu = __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer__["Layer"], {
            plain: true,
            onEsc: this.onCloseMenu,
            position: "left",
            onClickOverlay: this.onCloseMenu,
            __source: {
              fileName: _jsxFileName,
              lineNumber: 96
            }
          }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__["Box"], {
            background: "brand",
            gap: "small",
            style: {
              height: '100vh'
            },
            pad: "medium",
            align: "start",
            __source: {
              fileName: _jsxFileName,
              lineNumber: 97
            }
          }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_8_grommet_components_Button__["Button"], {
            icon: __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu__["Menu"], {
              __source: {
                fileName: _jsxFileName,
                lineNumber: 98
              }
            }),
            onClick: this.onResponsiveMenu,
            __source: {
              fileName: _jsxFileName,
              lineNumber: 98
            }
          }), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14__RoutedAnchor__["a" /* default */], {
            path: "/",
            label: "home",
            a11yTitle: "go to home page",
            __source: {
              fileName: _jsxFileName,
              lineNumber: 99
            }
          }), items, connectBitwigStudio, themeDesigner, themeSelector));
        }
      } else if (isWide) {
        menu = __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__["Box"], {
          direction: "row",
          align: "center",
          justify: "end",
          gap: "small",
          tag: "nav",
          __source: {
            fileName: _jsxFileName,
            lineNumber: 110
          }
        }, items, connectBitwigStudio, themeSelector, themeDesigner);
      }

      return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__["Box"], {
        tag: "header",
        direction: "row-responsive",
        justify: "between",
        align: "center",
        background: "brand",
        pad: "small",
        animation: "fadeIn",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 119
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__["Box"], {
        direction: "row",
        align: "center",
        gap: "small",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 127
        }
      }, isNarrow && __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_8_grommet_components_Button__["Button"], {
        icon: __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu__["Menu"], {
          __source: {
            fileName: _jsxFileName,
            lineNumber: 129
          }
        }),
        onClick: this.onResponsiveMenu,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 129
        }
      }), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading__["Heading"], {
        level: 2,
        margin: "none",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 131
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_13__RoutedButton__["a" /* default */], {
        path: "/",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 132
        }
      }, "bitwig-websocket-rpc"))), menu);
    }
  }]);

  return Header;
}(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Component);

Header.defaultProps = {
  size: undefined
};
Header.propTypes = {
  title: __WEBPACK_IMPORTED_MODULE_2_prop_types___default.a.string.isRequired,
  size: __WEBPACK_IMPORTED_MODULE_2_prop_types___default.a.string
};

var mapDispatchToProps = function mapDispatchToProps(dispatch) {
  return Object(__WEBPACK_IMPORTED_MODULE_3_redux__["bindActionCreators"])({
    selectTheme: __WEBPACK_IMPORTED_MODULE_16__redux_themes_actions__["a" /* selectTheme */]
  }, dispatch);
};

var mapStateToProps = function mapStateToProps(state) {
  return {
    themes: state.themes
  };
};

/* harmony default export */ __webpack_exports__["a"] = (Object(__WEBPACK_IMPORTED_MODULE_1_next_router__["withRouter"])(Object(__WEBPACK_IMPORTED_MODULE_12__redux__["a" /* default */])(mapStateToProps, mapDispatchToProps)(Header)));

/***/ })

})
//# sourceMappingURL=5.5e8a8cb0f58f51e132b3.hot-update.js.map