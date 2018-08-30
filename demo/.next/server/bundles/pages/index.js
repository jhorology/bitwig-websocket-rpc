module.exports =
/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		var threw = true;
/******/ 		try {
/******/ 			modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/ 			threw = false;
/******/ 		} finally {
/******/ 			if(threw) delete installedModules[moduleId];
/******/ 		}
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 3);
/******/ })
/************************************************************************/
/******/ ({

/***/ "./components/Anchor.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__nextjs_Anchor__ = __webpack_require__("./components/nextjs/Anchor.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/Anchor.js";


function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }


/* harmony default export */ __webpack_exports__["a"] = (function (props) {
  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1__nextjs_Anchor__["a" /* default */], _extends({
    preserveParams: "theme"
  }, props, {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 4
    }
  }));
});

/***/ }),

/***/ "./components/ColorRoll.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Text__ = __webpack_require__("grommet/components/Text");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Text___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Text__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_contexts_ThemeContext__ = __webpack_require__("grommet/contexts/ThemeContext");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_contexts_ThemeContext___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_grommet_contexts_ThemeContext__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_utils_colors__ = __webpack_require__("grommet/utils/colors");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_utils_colors___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_grommet_utils_colors__);
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/ColorRoll.js";


function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

function _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _nonIterableSpread(); }

function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance"); }

function _iterableToArray(iter) { if (Symbol.iterator in Object(iter) || Object.prototype.toString.call(iter) === "[object Arguments]") return Array.from(iter); }

function _arrayWithoutHoles(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = new Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } }






var colorList = function colorList(theme, prefix) {
  return Object.keys(theme.global.colors).filter(function (c) {
    return c.startsWith(prefix);
  });
};

/* unused harmony default export */ var _unused_webpack_default_export = (function (_ref) {
  var _ref$basis = _ref.basis,
      basis = _ref$basis === void 0 ? 'small' : _ref$basis,
      _ref$extended = _ref.extended,
      extended = _ref$extended === void 0 ? false : _ref$extended;

  var roll = function roll(theme) {
    var colors = ['brand'];
    colors = _toConsumableArray(colors).concat(_toConsumableArray(colorList(theme, 'accent')));
    colors = _toConsumableArray(colors).concat(_toConsumableArray(colorList(theme, 'neutral')));
    colors = _toConsumableArray(colors).concat(_toConsumableArray(colorList(theme, 'status')));

    if (extended) {
      colors = _toConsumableArray(colors).concat(_toConsumableArray(colorList(theme, 'light')));
      colors = _toConsumableArray(colors).concat(_toConsumableArray(colorList(theme, 'dark')));
    }

    function parseHexToRGB(color) {
      // https://stackoverflow.com/a/42429333
      if (!color.match) {
        return [255, 255, 255];
      }

      return color.match(/[A-Za-z0-9]{2}/g).map(function (v) {
        return parseInt(v, 16);
      });
    }

    function getRGBArray(color) {
      if (/^#/.test(color)) {
        return parseHexToRGB(color);
      } else if (/^rgb/.test(color)) {
        return color.match(/rgba?\((\s?[0-9]*\s?),(\s?[0-9]*\s?),(\s?[0-9]*\s?).*?\)/).splice(1);
      }

      return color;
    }

    var colorIsDark = function colorIsDark(color) {
      if (color.critical) {
        return false;
      }

      var _getRGBArray = getRGBArray(color),
          _getRGBArray2 = _slicedToArray(_getRGBArray, 3),
          red = _getRGBArray2[0],
          green = _getRGBArray2[1],
          blue = _getRGBArray2[2]; // http://www.had2know.com/technology/
      //  color-contrast-calculator-web-design.html


      var brightness = (299 * red + 587 * green + 114 * blue) / 1000;
      return brightness < 125;
    };

    var darkColor = colorIsDark(theme.global.colors.text) ? theme.global.text.color.light : theme.global.text.color.dark;
    var lightColor = colorIsDark(theme.global.colors.text) ? theme.global.text.color.dark : theme.global.text.color.light;
    return colors.map(function (color, index) {
      var rgb = Object(__WEBPACK_IMPORTED_MODULE_4_grommet_utils_colors__["colorForName"])(color, theme);
      var textColor = colorIsDark(rgb) ? lightColor : darkColor;
      return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
        key: "rgb-".concat(index),
        basis: basis,
        pad: "small",
        background: rgb,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 57
        }
      }, basis === 'small' && __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Text__["Text"], {
        color: textColor,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 59
        }
      }, color));
    });
  };

  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    flex: true,
    direction: "row",
    wrap: true,
    fill: "horizontal",
    __source: {
      fileName: _jsxFileName,
      lineNumber: 68
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_3_grommet_contexts_ThemeContext__["ThemeContext"].Consumer, {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 69
    }
  }, function (theme) {
    return roll(theme);
  }));
});

/***/ }),

/***/ "./components/Footer.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Anchor__ = __webpack_require__("grommet/components/Anchor");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Anchor___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Anchor__);
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/Footer.js";



/* harmony default export */ __webpack_exports__["a"] = (function () {
  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    tag: "footer",
    direction: "row",
    justify: "center",
    pad: {
      top: 'medium'
    },
    justifySelf: true,
    __source: {
      fileName: _jsxFileName,
      lineNumber: 5
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    basis: "large",
    border: "top",
    direction: "row",
    justify: "center",
    pad: "medium",
    gap: "medium",
    __source: {
      fileName: _jsxFileName,
      lineNumber: 12
    }
  }, "made with", __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Anchor__["Anchor"], {
    href: "https://github.com/grommet/grommet/tree/NEXT",
    target: "_blank",
    label: "grommet v2",
    a11yTitle: "Go to the github page for Grommet 2",
    __source: {
      fileName: _jsxFileName,
      lineNumber: 21
    }
  }), "based on", __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Anchor__["Anchor"], {
    href: "https://github.com/atanasster/grommet-nextjs",
    target: "_blank",
    label: "atanasster/grommet-nextjs",
    a11yTitle: "Go to the github page for next-sample",
    __source: {
      fileName: _jsxFileName,
      lineNumber: 28
    }
  })));
});

/***/ }),

/***/ "./components/Header.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_next_router__ = __webpack_require__("next/router");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_next_router___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_next_router__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prop_types__ = __webpack_require__("prop-types");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prop_types___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_prop_types__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux__ = __webpack_require__("redux");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_redux__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading__ = __webpack_require__("grommet/components/Heading");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_grommet_components_Heading__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_Select__ = __webpack_require__("grommet/components/Select");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_Select___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_grommet_components_Select__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer__ = __webpack_require__("grommet/components/Layer");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_7_grommet_components_Layer__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_grommet_components_Button__ = __webpack_require__("grommet/components/Button");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_grommet_components_Button___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_8_grommet_components_Button__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System__ = __webpack_require__("grommet-icons/icons/System");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_9_grommet_icons_icons_System__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu__ = __webpack_require__("grommet-icons/icons/Menu");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_10_grommet_icons_icons_Menu__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11_grommet_icons_icons_Connect__ = __webpack_require__("grommet-icons/icons/Connect");
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

/***/ }),

/***/ "./components/Item.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Heading__ = __webpack_require__("grommet/components/Heading");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Heading___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Heading__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__RoutedAnchor__ = __webpack_require__("./components/RoutedAnchor.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/Item.js";




/* harmony default export */ __webpack_exports__["a"] = (function (_ref) {
  var name = _ref.name,
      path = _ref.path,
      children = _ref.children,
      center = _ref.center;
  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    basis: "medium",
    margin: {
      right: 'medium',
      bottom: 'medium'
    },
    __source: {
      fileName: _jsxFileName,
      lineNumber: 7
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_3__RoutedAnchor__["a" /* default */], {
    path: path,
    __source: {
      fileName: _jsxFileName,
      lineNumber: 8
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 9
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Heading__["Heading"], {
    level: 3,
    size: "small",
    margin: {
      top: 'none',
      bottom: 'small'
    },
    __source: {
      fileName: _jsxFileName,
      lineNumber: 10
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("strong", {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 11
    }
  }, name)))), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 15
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
    basis: "small",
    border: {
      color: 'brand',
      size: 'medium'
    },
    justify: center ? 'center' : undefined,
    align: center ? 'center' : undefined,
    pad: center ? 'medium' : undefined,
    style: {
      overflow: 'hidden'
    },
    __source: {
      fileName: _jsxFileName,
      lineNumber: 16
    }
  }, children)));
});

/***/ }),

/***/ "./components/Page.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_next_router__ = __webpack_require__("next/router");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_next_router___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_next_router__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prop_types__ = __webpack_require__("prop-types");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_prop_types___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_prop_types__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux__ = __webpack_require__("redux");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_redux__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_next_head__ = __webpack_require__("next/head");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_next_head___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_next_head__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Grommet__ = __webpack_require__("grommet/components/Grommet");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Grommet___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_grommet_components_Grommet__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_contexts_ResponsiveContext__ = __webpack_require__("grommet/contexts/ResponsiveContext");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_contexts_ResponsiveContext___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_7_grommet_contexts_ResponsiveContext__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__Header__ = __webpack_require__("./components/Header.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9__Footer__ = __webpack_require__("./components/Footer.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10__redux__ = __webpack_require__("./redux/index.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11__redux_themes_actions__ = __webpack_require__("./redux/themes/actions.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12__utils_analytics__ = __webpack_require__("./components/utils/analytics.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/Page.js";

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }















var Page =
/*#__PURE__*/
function (_React$Component) {
  _inherits(Page, _React$Component);

  function Page() {
    _classCallCheck(this, Page);

    return _possibleConstructorReturn(this, (Page.__proto__ || Object.getPrototypeOf(Page)).apply(this, arguments));
  }

  _createClass(Page, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      if (!window.GA_INITIALIZED) {
        Object(__WEBPACK_IMPORTED_MODULE_12__utils_analytics__["a" /* initGA */])();
        window.GA_INITIALIZED = true;
      }

      Object(__WEBPACK_IMPORTED_MODULE_12__utils_analytics__["b" /* logPageView */])();
    }
  }, {
    key: "render",
    value: function render() {
      var _props = this.props,
          children = _props.children,
          pageTitle = _props.title,
          description = _props.description,
          _props$themes = _props.themes,
          themes = _props$themes.themes,
          theme = _props$themes.selected;
      var keywords = ['grommet', 'grommet 2', 'react', 'next-js', 'next.js', 'ui library'];

      if (pageTitle) {
        keywords.push(pageTitle);
      }

      return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Fragment, {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 33
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_next_head___default.a, {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 34
        }
      }, pageTitle && __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("title", {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 36
        }
      }, "Grommet - ".concat(pageTitle)), typeof description === 'string' && __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("meta", {
        name: "description",
        content: description,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 40
        }
      }), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("meta", {
        name: "keywords",
        content: keywords.join(','),
        __source: {
          fileName: _jsxFileName,
          lineNumber: 43
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_5_grommet_components_Grommet__["Grommet"], {
        theme: themes[theme] || {},
        style: {
          height: 'auto',
          minHeight: '100vh'
        },
        __source: {
          fileName: _jsxFileName,
          lineNumber: 45
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_7_grommet_contexts_ResponsiveContext__["ResponsiveContext"].Consumer, {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 46
        }
      }, function (size) {
        return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_6_grommet_components_Box__["Box"], {
          style: {
            height: 'auto',
            minHeight: '100vh'
          },
          __source: {
            fileName: _jsxFileName,
            lineNumber: 48
          }
        }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_8__Header__["a" /* default */], {
          title: pageTitle,
          size: size,
          __source: {
            fileName: _jsxFileName,
            lineNumber: 49
          }
        }), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_6_grommet_components_Box__["Box"], {
          flex: true,
          __source: {
            fileName: _jsxFileName,
            lineNumber: 50
          }
        }, children), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_9__Footer__["a" /* default */], {
          __source: {
            fileName: _jsxFileName,
            lineNumber: 53
          }
        }));
      })));
    }
  }]);

  return Page;
}(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Component);

Page.propTypes = {
  title: __WEBPACK_IMPORTED_MODULE_2_prop_types___default.a.string.isRequired,
  description: __WEBPACK_IMPORTED_MODULE_2_prop_types___default.a.string
};
Page.defaultProps = {
  description: undefined
};

var mapDispatchToProps = function mapDispatchToProps(dispatch) {
  return Object(__WEBPACK_IMPORTED_MODULE_3_redux__["bindActionCreators"])({
    selectTheme: __WEBPACK_IMPORTED_MODULE_11__redux_themes_actions__["a" /* selectTheme */]
  }, dispatch);
};

var mapStateToProps = function mapStateToProps(state) {
  return {
    themes: state.themes
  };
};

/* harmony default export */ __webpack_exports__["a"] = (Object(__WEBPACK_IMPORTED_MODULE_1_next_router__["withRouter"])(Object(__WEBPACK_IMPORTED_MODULE_10__redux__["a" /* default */])(mapStateToProps, mapDispatchToProps)(Page)));

/***/ }),

/***/ "./components/RoutedAnchor.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__nextjs_RoutedAnchor__ = __webpack_require__("./components/nextjs/RoutedAnchor.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/RoutedAnchor.js";


function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }


/* harmony default export */ __webpack_exports__["a"] = (function (props) {
  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1__nextjs_RoutedAnchor__["a" /* default */], _extends({
    preserveParams: "theme"
  }, props, {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 4
    }
  }));
});

/***/ }),

/***/ "./components/RoutedButton.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__nextjs_RoutedButton__ = __webpack_require__("./components/nextjs/RoutedButton.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/RoutedButton.js";


function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }


/* harmony default export */ __webpack_exports__["a"] = (function (props) {
  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1__nextjs_RoutedButton__["a" /* default */], _extends({
    preserveParams: "theme"
  }, props, {
    __source: {
      fileName: _jsxFileName,
      lineNumber: 4
    }
  }));
});

/***/ }),

/***/ "./components/Section.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Heading__ = __webpack_require__("grommet/components/Heading");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Heading___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Heading__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Box__);
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/Section.js";



/* harmony default export */ __webpack_exports__["a"] = (function (_ref) {
  var children = _ref.children,
      index = _ref.index,
      name = _ref.name;
  return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Box__["Box"], {
    pad: {
      vertical: 'medium'
    },
    animation: [{
      type: 'zoomIn',
      duration: 500,
      delay: 100 + 100 * index
    }, {
      type: 'fadeIn',
      duration: 500,
      delay: 100 * index
    }],
    __source: {
      fileName: _jsxFileName,
      lineNumber: 4
    }
  }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Heading__["Heading"], {
    level: 2,
    margin: {
      top: 'none'
    },
    __source: {
      fileName: _jsxFileName,
      lineNumber: 11
    }
  }, name), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Box__["Box"], {
    direction: "row",
    wrap: true,
    __source: {
      fileName: _jsxFileName,
      lineNumber: 14
    }
  }, children));
});

/***/ }),

/***/ "./components/nextjs/Anchor.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prop_types__ = __webpack_require__("prop-types");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prop_types___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_prop_types__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_next_router__ = __webpack_require__("next/router");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_next_router___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_next_router__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor__ = __webpack_require__("grommet/components/Anchor");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__urlParams__ = __webpack_require__("./components/nextjs/urlParams.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/nextjs/Anchor.js";


function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }






var Anchor =
/*#__PURE__*/
function (_React$Component) {
  _inherits(Anchor, _React$Component);

  function Anchor() {
    _classCallCheck(this, Anchor);

    return _possibleConstructorReturn(this, (Anchor.__proto__ || Object.getPrototypeOf(Anchor)).apply(this, arguments));
  }

  _createClass(Anchor, [{
    key: "render",
    value: function render() {
      var _props = this.props,
          path = _props.path,
          preserveParams = _props.preserveParams,
          router = _props.router,
          rest = _objectWithoutProperties(_props, ["path", "preserveParams", "router"]);

      var href = Object(__WEBPACK_IMPORTED_MODULE_4__urlParams__["a" /* default */])(path, router, preserveParams);
      return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor__["Anchor"], _extends({
        href: href
      }, rest, {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 13
        }
      }));
    }
  }]);

  return Anchor;
}(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Component);

Anchor.defaultProps = {
  preserveParams: undefined
};
Anchor.propTypes = {
  path: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string.isRequired,
  preserveParams: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.oneOfType([__WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string, __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.array])
};
/* harmony default export */ __webpack_exports__["a"] = (Object(__WEBPACK_IMPORTED_MODULE_2_next_router__["withRouter"])(Anchor));

/***/ }),

/***/ "./components/nextjs/RoutedAnchor.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prop_types__ = __webpack_require__("prop-types");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prop_types___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_prop_types__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_next_router__ = __webpack_require__("next/router");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_next_router___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_next_router__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor__ = __webpack_require__("grommet/components/Anchor");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_next_link__ = __webpack_require__("next/link");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_next_link___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_next_link__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__urlParams__ = __webpack_require__("./components/nextjs/urlParams.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/nextjs/RoutedAnchor.js";


function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }







var RoutedAnchor =
/*#__PURE__*/
function (_React$Component) {
  _inherits(RoutedAnchor, _React$Component);

  function RoutedAnchor() {
    _classCallCheck(this, RoutedAnchor);

    return _possibleConstructorReturn(this, (RoutedAnchor.__proto__ || Object.getPrototypeOf(RoutedAnchor)).apply(this, arguments));
  }

  _createClass(RoutedAnchor, [{
    key: "render",
    value: function render() {
      var _props = this.props,
          path = _props.path,
          preserveParams = _props.preserveParams,
          router = _props.router,
          params = _props.params,
          rest = _objectWithoutProperties(_props, ["path", "preserveParams", "router", "params"]);

      var query = Object(__WEBPACK_IMPORTED_MODULE_5__urlParams__["b" /* queryParams */])(router, preserveParams);
      return (// eslint-disable-next-line jsx-a11y/anchor-is-valid
        __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_next_link___default.a, {
          href: {
            pathname: path,
            query: query
          },
          passHref: true,
          __source: {
            fileName: _jsxFileName,
            lineNumber: 16
          }
        }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Anchor__["Anchor"], _extends({}, rest, {
          __source: {
            fileName: _jsxFileName,
            lineNumber: 20
          }
        })))
      );
    }
  }]);

  return RoutedAnchor;
}(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Component);

RoutedAnchor.defaultProps = {
  preserveParams: undefined,
  path: undefined,
  route: undefined
};
RoutedAnchor.propTypes = {
  path: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string,
  route: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string,
  preserveParams: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.oneOfType([__WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string, __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.array])
};
/* harmony default export */ __webpack_exports__["a"] = (Object(__WEBPACK_IMPORTED_MODULE_2_next_router__["withRouter"])(RoutedAnchor));

/***/ }),

/***/ "./components/nextjs/RoutedButton.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prop_types__ = __webpack_require__("prop-types");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_prop_types___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_prop_types__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_next_router__ = __webpack_require__("next/router");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_next_router___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_next_router__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Button__ = __webpack_require__("grommet/components/Button");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Button___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Button__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_next_link__ = __webpack_require__("next/link");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_next_link___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_next_link__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__urlParams__ = __webpack_require__("./components/nextjs/urlParams.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/components/nextjs/RoutedButton.js";


function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }







var RoutedButton =
/*#__PURE__*/
function (_React$Component) {
  _inherits(RoutedButton, _React$Component);

  function RoutedButton() {
    _classCallCheck(this, RoutedButton);

    return _possibleConstructorReturn(this, (RoutedButton.__proto__ || Object.getPrototypeOf(RoutedButton)).apply(this, arguments));
  }

  _createClass(RoutedButton, [{
    key: "render",
    value: function render() {
      var _props = this.props,
          path = _props.path,
          preserveParams = _props.preserveParams,
          router = _props.router,
          params = _props.params,
          rest = _objectWithoutProperties(_props, ["path", "preserveParams", "router", "params"]);

      var query = Object(__WEBPACK_IMPORTED_MODULE_5__urlParams__["b" /* queryParams */])(router, preserveParams);
      return (// eslint-disable-next-line jsx-a11y/anchor-is-valid
        __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_4_next_link___default.a, {
          href: {
            pathname: path,
            query: query
          },
          passHref: true,
          __source: {
            fileName: _jsxFileName,
            lineNumber: 15
          }
        }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Button__["Button"], _extends({}, rest, {
          __source: {
            fileName: _jsxFileName,
            lineNumber: 20
          }
        })))
      );
    }
  }]);

  return RoutedButton;
}(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Component);

RoutedButton.defaultProps = {
  preserveParams: undefined,
  path: undefined,
  route: undefined
};
RoutedButton.propTypes = {
  path: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string,
  route: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string,
  preserveParams: __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.oneOfType([__WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.string, __WEBPACK_IMPORTED_MODULE_1_prop_types___default.a.array])
};
/* harmony default export */ __webpack_exports__["a"] = (Object(__WEBPACK_IMPORTED_MODULE_2_next_router__["withRouter"])(RoutedButton));

/***/ }),

/***/ "./components/nextjs/urlParams.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return queryParams; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_url_search_params__ = __webpack_require__("url-search-params");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_url_search_params___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_url_search_params__);

/* harmony default export */ __webpack_exports__["a"] = (function (newPath, router, preserveParams) {
  var href = newPath;

  if (preserveParams) {
    var query = typeof preserveParams === 'string' ? [preserveParams] : preserveParams;
    var params = new __WEBPACK_IMPORTED_MODULE_0_url_search_params___default.a(router.asPath.split('?')[1]);
    query.forEach(function (p) {
      if (router.query[p] !== undefined) {
        params.set(p, router.query[p]);
      }
    });

    if (Array.from(params.keys()).length !== 0) {
      href = "".concat(newPath, "?").concat(params.toString());
    }
  }

  return href;
});
var queryParams = function queryParams(router, preserveParams) {
  var result = {};

  if (preserveParams) {
    var query = typeof preserveParams === 'string' ? [preserveParams] : preserveParams;
    var params = new __WEBPACK_IMPORTED_MODULE_0_url_search_params___default.a(router.asPath.split('?')[1]); // eslint-disable-next-line no-restricted-syntax

    var _iteratorNormalCompletion = true;
    var _didIteratorError = false;
    var _iteratorError = undefined;

    try {
      for (var _iterator = params.entries()[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
        var _pair = _step.value;

        if (query.indexOf(_pair[0]) !== -1) {
          // eslint-disable-next-line prefer-destructuring
          result[_pair[0]] = _pair[1];
        }
      }
    } catch (err) {
      _didIteratorError = true;
      _iteratorError = err;
    } finally {
      try {
        if (!_iteratorNormalCompletion && _iterator.return != null) {
          _iterator.return();
        }
      } finally {
        if (_didIteratorError) {
          throw _iteratorError;
        }
      }
    }
  }

  return result;
};

/***/ }),

/***/ "./components/utils/analytics.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return initGA; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return logPageView; });
/* unused harmony export logEvent */
/* unused harmony export logException */
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react_ga__ = __webpack_require__("react-ga");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react_ga___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react_ga__);

var initGA = function initGA() {
  __WEBPACK_IMPORTED_MODULE_0_react_ga___default.a.initialize('UA-118001856-1');
};
var logPageView = function logPageView() {
  __WEBPACK_IMPORTED_MODULE_0_react_ga___default.a.set({
    page: window.location.pathname
  });
  __WEBPACK_IMPORTED_MODULE_0_react_ga___default.a.pageview(window.location.pathname);
};
var logEvent = function logEvent() {
  var category = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
  var action = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : '';

  if (category && action) {
    __WEBPACK_IMPORTED_MODULE_0_react_ga___default.a.event({
      category: category,
      action: action
    });
  }
};
var logException = function logException() {
  var description = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
  var fatal = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;

  if (description) {
    __WEBPACK_IMPORTED_MODULE_0_react_ga___default.a.exception({
      description: description,
      fatal: fatal
    });
  }
};

/***/ }),

/***/ "./pages/index.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "default", function() { return Home; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__ = __webpack_require__("grommet/components/Box");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_grommet_components_Box___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Button__ = __webpack_require__("grommet/components/Button");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_grommet_components_Button___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_grommet_components_Button__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Calendar__ = __webpack_require__("grommet/components/Calendar");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_components_Calendar___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_grommet_components_Calendar__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_components_Carousel__ = __webpack_require__("grommet/components/Carousel");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_components_Carousel___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_grommet_components_Carousel__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Chart__ = __webpack_require__("grommet/components/Chart");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_components_Chart___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_grommet_components_Chart__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_CheckBox__ = __webpack_require__("grommet/components/CheckBox");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_components_CheckBox___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_grommet_components_CheckBox__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_components_Clock__ = __webpack_require__("grommet/components/Clock");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_grommet_components_Clock___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_7_grommet_components_Clock__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_grommet_components_Diagram__ = __webpack_require__("grommet/components/Diagram");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8_grommet_components_Diagram___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_8_grommet_components_Diagram__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9_grommet_components_Distribution__ = __webpack_require__("grommet/components/Distribution");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9_grommet_components_Distribution___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_9_grommet_components_Distribution__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10_grommet_components_DropButton__ = __webpack_require__("grommet/components/DropButton");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10_grommet_components_DropButton___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_10_grommet_components_DropButton__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11_grommet_components_Anchor__ = __webpack_require__("grommet/components/Anchor");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11_grommet_components_Anchor___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_11_grommet_components_Anchor__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12_grommet_components_FormField__ = __webpack_require__("grommet/components/FormField");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12_grommet_components_FormField___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_12_grommet_components_FormField__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_13_grommet_components_Heading__ = __webpack_require__("grommet/components/Heading");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_13_grommet_components_Heading___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_13_grommet_components_Heading__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__ = __webpack_require__("grommet/components/Image");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_14_grommet_components_Image___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_15_grommet_components_RangeSelector__ = __webpack_require__("grommet/components/RangeSelector");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_15_grommet_components_RangeSelector___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_15_grommet_components_RangeSelector__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_16_grommet_components_DataTable__ = __webpack_require__("grommet/components/DataTable");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_16_grommet_components_DataTable___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_16_grommet_components_DataTable__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_17_grommet_components_Accordion__ = __webpack_require__("grommet/components/Accordion");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_17_grommet_components_Accordion___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_17_grommet_components_Accordion__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_18_grommet_components_AccordionPanel__ = __webpack_require__("grommet/components/AccordionPanel");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_18_grommet_components_AccordionPanel___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_18_grommet_components_AccordionPanel__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_19_grommet_components_Menu__ = __webpack_require__("grommet/components/Menu");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_19_grommet_components_Menu___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_19_grommet_components_Menu__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_20_grommet_components_Meter__ = __webpack_require__("grommet/components/Meter");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_20_grommet_components_Meter___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_20_grommet_components_Meter__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_21_grommet_components_Paragraph__ = __webpack_require__("grommet/components/Paragraph");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_21_grommet_components_Paragraph___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_21_grommet_components_Paragraph__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_22_grommet_components_RadioButton__ = __webpack_require__("grommet/components/RadioButton");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_22_grommet_components_RadioButton___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_22_grommet_components_RadioButton__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_23_grommet_components_RangeInput__ = __webpack_require__("grommet/components/RangeInput");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_23_grommet_components_RangeInput___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_23_grommet_components_RangeInput__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_24_grommet_components_Select__ = __webpack_require__("grommet/components/Select");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_24_grommet_components_Select___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_24_grommet_components_Select__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_25_grommet_components_Stack__ = __webpack_require__("grommet/components/Stack");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_25_grommet_components_Stack___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_25_grommet_components_Stack__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_26_grommet_components_Table__ = __webpack_require__("grommet/components/Table");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_26_grommet_components_Table___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_26_grommet_components_Table__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_27_grommet_components_TableBody__ = __webpack_require__("grommet/components/TableBody");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_27_grommet_components_TableBody___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_27_grommet_components_TableBody__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_28_grommet_components_TableCell__ = __webpack_require__("grommet/components/TableCell");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_28_grommet_components_TableCell___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_28_grommet_components_TableCell__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_29_grommet_components_TableHeader__ = __webpack_require__("grommet/components/TableHeader");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_29_grommet_components_TableHeader___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_29_grommet_components_TableHeader__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_30_grommet_components_TableRow__ = __webpack_require__("grommet/components/TableRow");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_30_grommet_components_TableRow___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_30_grommet_components_TableRow__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_31_grommet_components_Text__ = __webpack_require__("grommet/components/Text");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_31_grommet_components_Text___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_31_grommet_components_Text__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_32_grommet_components_TextArea__ = __webpack_require__("grommet/components/TextArea");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_32_grommet_components_TextArea___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_32_grommet_components_TextArea__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_33_grommet_components_TextInput__ = __webpack_require__("grommet/components/TextInput");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_33_grommet_components_TextInput___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_33_grommet_components_TextInput__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_34_grommet_components_Video__ = __webpack_require__("grommet/components/Video");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_34_grommet_components_Video___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_34_grommet_components_Video__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_35_grommet_components_WorldMap__ = __webpack_require__("grommet/components/WorldMap");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_35_grommet_components_WorldMap___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_35_grommet_components_WorldMap__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_36_grommet_icons_icons_Add__ = __webpack_require__("grommet-icons/icons/Add");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_36_grommet_icons_icons_Add___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_36_grommet_icons_icons_Add__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_37_grommet_icons_icons_LinkNext__ = __webpack_require__("grommet-icons/icons/LinkNext");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_37_grommet_icons_icons_LinkNext___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_37_grommet_icons_icons_LinkNext__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_38_grommet_icons_icons_Grommet__ = __webpack_require__("grommet-icons/icons/Grommet");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_38_grommet_icons_icons_Grommet___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_38_grommet_icons_icons_Grommet__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_39_grommet_icons_icons_Descend__ = __webpack_require__("grommet-icons/icons/Descend");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_39_grommet_icons_icons_Descend___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_39_grommet_icons_icons_Descend__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_40__components_RoutedButton__ = __webpack_require__("./components/RoutedButton.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_41__components_Page__ = __webpack_require__("./components/Page.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_42__components_Section__ = __webpack_require__("./components/Section.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_43__components_Item__ = __webpack_require__("./components/Item.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_44__components_ColorRoll__ = __webpack_require__("./components/ColorRoll.js");
var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/pages/index.js";


function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }













































var CHART_VALUES = [{
  value: [7, 90],
  label: 'ninety'
}, {
  value: [6, 80],
  label: 'eighty'
}, {
  value: [5, 60],
  label: 'sixty'
}, {
  value: [4, 70],
  label: 'seventy'
}, {
  value: [3, 60],
  label: 'sixty'
}, {
  value: [2, 40],
  label: 'forty'
}, {
  value: [1, 30],
  label: 'thirty'
}, {
  value: [0, 10],
  label: 'ten'
}];
var stringOptions = ['small', 'medium', 'large', 'xlarge', 'huge'];

var Home =
/*#__PURE__*/
function (_React$Component) {
  _inherits(Home, _React$Component);

  function Home() {
    var _ref;

    var _temp, _this;

    _classCallCheck(this, Home);

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    return _possibleConstructorReturn(_this, (_temp = _this = _possibleConstructorReturn(this, (_ref = Home.__proto__ || Object.getPrototypeOf(Home)).call.apply(_ref, [this].concat(args))), Object.defineProperty(_assertThisInitialized(_this), "state", {
      configurable: true,
      enumerable: true,
      writable: true,
      value: {
        values: [3, 7]
      }
    }), _temp));
  }

  _createClass(Home, [{
    key: "render",
    value: function render() {
      var values = this.state.values;
      return __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_41__components_Page__["a" /* default */], {
        title: "Explore",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 34
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
        pad: "large",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 35
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
        direction: "row",
        gap: "xlarge",
        margin: {
          bottom: 'large'
        },
        __source: {
          fileName: _jsxFileName,
          lineNumber: 36
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
        basis: "large",
        overflow: "hidden",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 37
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_13_grommet_components_Heading__["Heading"], {
        level: 1,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 38
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("strong", {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 39
        }
      }, "bitwig-websocket-rpc demo")), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_21_grommet_components_Paragraph__["Paragraph"], {
        size: "large",
        margin: "none",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 41
        }
      }, "This is an experimental site built with ", __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("strong", {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 42
        }
      }, "Grommet 2"), " and ", __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement("strong", {
        __source: {
          fileName: _jsxFileName,
          lineNumber: 42
        }
      }, "Next.js"), ". Visit the official ", __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_11_grommet_components_Anchor__["Anchor"], {
        href: "https://v2.grommet.io/",
        target: "_blank",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 43
        }
      }, "Grommet site"), " for the latest updates.")))), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_1_grommet_components_Box__["Box"], {
        pad: {
          horizontal: 'large'
        },
        __source: {
          fileName: _jsxFileName,
          lineNumber: 48
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_42__components_Section__["a" /* default */], {
        name: "Start",
        index: 0,
        __source: {
          fileName: _jsxFileName,
          lineNumber: 49
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 50
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 51
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 56
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 57
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 62
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 63
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 68
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 69
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 74
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 75
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 80
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 81
        }
      })), __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_43__components_Item__["a" /* default */], {
        name: "Image",
        path: "/image",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 86
        }
      }, __WEBPACK_IMPORTED_MODULE_0_react___default.a.createElement(__WEBPACK_IMPORTED_MODULE_14_grommet_components_Image__["Image"], {
        fit: "cover",
        src: "//v2.grommet.io/assets/Wilderpeople_Ricky.jpg",
        __source: {
          fileName: _jsxFileName,
          lineNumber: 87
        }
      })))));
    }
  }]);

  return Home;
}(__WEBPACK_IMPORTED_MODULE_0_react___default.a.Component);



/***/ }),

/***/ "./redux/index.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export store */
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__babel_runtime_regenerator__ = __webpack_require__("@babel/runtime/regenerator");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__babel_runtime_regenerator___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0__babel_runtime_regenerator__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_react__ = __webpack_require__("react");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_react___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_react__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_recompose_getDisplayName__ = __webpack_require__("recompose/getDisplayName");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_recompose_getDisplayName___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_recompose_getDisplayName__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux__ = __webpack_require__("redux");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_redux___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_redux__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_react_redux__ = __webpack_require__("react-redux");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_react_redux___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_react_redux__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_react_redux_lib_utils_PropTypes__ = __webpack_require__("react-redux/lib/utils/PropTypes");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_react_redux_lib_utils_PropTypes___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_react_redux_lib_utils_PropTypes__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_redux_devtools_extension__ = __webpack_require__("redux-devtools-extension");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_redux_devtools_extension___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_redux_devtools_extension__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_redux_thunk__ = __webpack_require__("redux-thunk");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7_redux_thunk___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_7_redux_thunk__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__themes_reducer__ = __webpack_require__("./redux/themes/reducer.js");

var _jsxFileName = "/Users/masafumi/Documents/GitHub/bitwig-websocket-rpc/demo/redux/index.js";

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _asyncToGenerator(fn) { return function () { var self = this, args = arguments; return new Promise(function (resolve, reject) { var gen = fn.apply(self, args); function step(key, arg) { try { var info = gen[key](arg); var value = info.value; } catch (error) { reject(error); return; } if (info.done) { resolve(value); } else { Promise.resolve(value).then(_next, _throw); } } function _next(value) { step("next", value); } function _throw(err) { step("throw", err); } _next(); }); }; }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }









var makeStore = Object(__WEBPACK_IMPORTED_MODULE_3_redux__["combineReducers"])({
  themes: __WEBPACK_IMPORTED_MODULE_8__themes_reducer__["a" /* default */]
});
var initialState = {};
var store = Object(__WEBPACK_IMPORTED_MODULE_3_redux__["createStore"])(makeStore, initialState, Object(__WEBPACK_IMPORTED_MODULE_6_redux_devtools_extension__["composeWithDevTools"])(Object(__WEBPACK_IMPORTED_MODULE_3_redux__["applyMiddleware"])(__WEBPACK_IMPORTED_MODULE_7_redux_thunk___default.a)));
/* harmony default export */ __webpack_exports__["a"] = (function () {
  for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
    args[_key] = arguments[_key];
  }

  return function (WrappedComponent) {
    var ConnectedWrapped = __WEBPACK_IMPORTED_MODULE_4_react_redux__["connect"].apply(void 0, args)(WrappedComponent);

    var ContextProvider =
    /*#__PURE__*/
    function (_React$Component) {
      _inherits(ContextProvider, _React$Component);

      function ContextProvider() {
        _classCallCheck(this, ContextProvider);

        return _possibleConstructorReturn(this, (ContextProvider.__proto__ || Object.getPrototypeOf(ContextProvider)).apply(this, arguments));
      }

      _createClass(ContextProvider, [{
        key: "getChildContext",
        value: function getChildContext() {
          return {
            store: store
          };
        }
      }, {
        key: "render",
        value: function render() {
          return __WEBPACK_IMPORTED_MODULE_1_react___default.a.createElement(ConnectedWrapped, _extends({}, this.props, {
            __source: {
              fileName: _jsxFileName,
              lineNumber: 37
            }
          }));
        }
      }], [{
        key: "getInitialProps",
        value: function () {
          var _getInitialProps = _asyncToGenerator(
          /*#__PURE__*/
          __WEBPACK_IMPORTED_MODULE_0__babel_runtime_regenerator___default.a.mark(function _callee(ctx) {
            return __WEBPACK_IMPORTED_MODULE_0__babel_runtime_regenerator___default.a.wrap(function _callee$(_context) {
              while (1) {
                switch (_context.prev = _context.next) {
                  case 0:
                    if (!WrappedComponent.getInitialProps) {
                      _context.next = 2;
                      break;
                    }

                    return _context.abrupt("return", WrappedComponent.getInitialProps(ctx));

                  case 2:
                    return _context.abrupt("return", {});

                  case 3:
                  case "end":
                    return _context.stop();
                }
              }
            }, _callee, this);
          }));

          return function getInitialProps(_x) {
            return _getInitialProps.apply(this, arguments);
          };
        }()
      }]);

      return ContextProvider;
    }(__WEBPACK_IMPORTED_MODULE_1_react___default.a.Component);

    Object.defineProperty(ContextProvider, "childContextTypes", {
      configurable: true,
      enumerable: true,
      writable: true,
      value: {
        store: __WEBPACK_IMPORTED_MODULE_5_react_redux_lib_utils_PropTypes__["storeShape"]
      }
    });
    ContextProvider.displayName = __WEBPACK_IMPORTED_MODULE_2_recompose_getDisplayName___default()(WrappedComponent);
    return ContextProvider;
  };
});

/***/ }),

/***/ "./redux/themes/actions.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* unused harmony export updateTheme */
/* unused harmony export deleteTheme */
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return selectTheme; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__constants__ = __webpack_require__("./redux/themes/constants.js");

var updateTheme = function updateTheme(name, theme) {
  return {
    type: __WEBPACK_IMPORTED_MODULE_0__constants__["c" /* UPDATE_THEME */],
    name: name,
    theme: theme
  };
};
var deleteTheme = function deleteTheme(name) {
  return {
    type: __WEBPACK_IMPORTED_MODULE_0__constants__["a" /* DELETE_THEME */],
    name: name
  };
};
var selectTheme = function selectTheme(name) {
  return {
    type: __WEBPACK_IMPORTED_MODULE_0__constants__["b" /* SELECT_THEME */],
    name: name
  };
};

/***/ }),

/***/ "./redux/themes/constants.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "c", function() { return UPDATE_THEME; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return DELETE_THEME; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "b", function() { return SELECT_THEME; });
var UPDATE_THEME = 'UPDATE_THEME';
var DELETE_THEME = 'DELETE_THEME';
var SELECT_THEME = 'SELECT_THEME';

/***/ }),

/***/ "./redux/themes/reducer.js":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_grommet_utils_object__ = __webpack_require__("grommet/utils/object");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_grommet_utils_object___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_grommet_utils_object__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_polished__ = __webpack_require__("polished");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1_polished___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_1_polished__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_styled_components__ = __webpack_require__("styled-components");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2_styled_components___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_2_styled_components__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes__ = __webpack_require__("grommet-controls/themes");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils__ = __webpack_require__("grommet-controls/themes/utils");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_themes_dark__ = __webpack_require__("grommet/themes/dark");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5_grommet_themes_dark___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_5_grommet_themes_dark__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_themes_grommet__ = __webpack_require__("grommet/themes/grommet");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6_grommet_themes_grommet___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_6_grommet_themes_grommet__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7__constants__ = __webpack_require__("./redux/themes/constants.js");
var _this = this;

function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; var ownKeys = Object.keys(source); if (typeof Object.getOwnPropertySymbols === 'function') { ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) { return Object.getOwnPropertyDescriptor(source, sym).enumerable; })); } ownKeys.forEach(function (key) { _defineProperty(target, key, source[key]); }); } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

// eslint-disable-next-line camelcase








var defaultTheme = 'black';
var lightColors = ['#F6F6F6', '#EEEEEE', '#DDDDDD', '#CCCCCC', '#BBBBBB', '#AAAAAA'];
var darkColors = ['#333333', '#444444', '#555555', '#666666', '#777777', '#999999'];
var accentColors = ['#c7e673', '#6f8040', '#dfe6cf', '#99bf30', '#68458a', '#604080'];
var neutralColors = ['#dacfe6', '#7830bf', '#d56b89', '#804052', '#e6cfd5', '#bf3059'];
var colors = {
  'active': Object(__WEBPACK_IMPORTED_MODULE_1_polished__["rgba"])('#DDDDDD', 0.5),
  'brand': '#99cc33',
  'border': 'rgba(68, 68, 68, 0.6)',
  'background': 'rgb(255, 248, 225)',
  'text': 'rgb(68, 68, 68)',
  'placeholder': 'rgba(68, 68, 68, 0.5)'
};
Object(__WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils__["colorsFromArray"])(colors, accentColors, 'accent');
Object(__WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils__["colorsFromArray"])(colors, darkColors, 'dark');
Object(__WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils__["colorsFromArray"])(colors, lightColors, 'light');
Object(__WEBPACK_IMPORTED_MODULE_4_grommet_controls_themes_utils__["colorsFromArray"])(colors, neutralColors, 'neutral');
var custom = Object(__WEBPACK_IMPORTED_MODULE_0_grommet_utils_object__["deepFreeze"])({
  'global': {
    'colors': colors,
    'elevation': {
      'none': 'none',
      'xsmall': '0px 1px 2px rgba(68, 68, 68, 0.5)',
      'small': '0px 2px 4px rgba(68, 68, 68, 0.5)',
      'medium': '0px 3px 8px rgba(68, 68, 68, 0.5)',
      'large': '0px 6px 12px rgba(68, 68, 68, 0.5)',
      'xlarge': '0px 8px 16px rgba(68, 68, 68, 0.5)'
    },
    'drop': {
      'backgroundColor': 'rgb(255, 242, 201)',
      'shadow': '0px 3px 8px rgba(68, 68, 68, 0.5)',
      'border': {
        'radius': '2px'
      }
    },
    'input': {
      'border': {
        'radius': '4px'
      }
    },
    'font': {
      'family': "'Roboto', sans-serif",
      'face': "@font-face {\n  font-family: 'Roboto';\n  font-style: normal;\n  font-weight: 400;\n  src: local('Roboto'), local('Roboto-Regular'), url(https://fonts.gstatic.com/s/roboto/v18/KFOmCnqEu92Fr1Mu4mxP.ttf) format('truetype');\n}\n"
    }
  },
  'layer': {
    'backgroundColor': '#fff8e1',
    'overlayBackgroundColor': 'rgba(68, 68, 68, 0.5)',
    'border': {
      'radius': '4px'
    }
  },
  'icon': {
    extend: Object(__WEBPACK_IMPORTED_MODULE_2_styled_components__["css"])(["", " ", ""], function (props) {
      return props.dark && "\n        fill: ".concat(props.theme.global.text.color.dark, ";\n        stroke: ").concat(props.theme.global.text.color.dark, ";\n      ");
    }, function (props) {
      return props.light && "\n        fill: ".concat(props.theme.global.text.color.light, ";\n        stroke: ").concat(props.theme.global.text.color.light, ";\n      ");
    })
  },
  'checkBox': {
    'border': {
      'color': {
        'light': 'rgba(68, 68, 68, 0.6)',
        'dark': 'rgba(68, 68, 68, 0.6)'
      },
      'radius': '4px'
    },
    'toggle': {
      'radius': '2px'
    }
  },
  'anchor': {
    'color': 'rgb(102, 51, 204)'
  },
  'radioButton': {
    'border': {
      'color': {
        'light': 'rgba(68, 68, 68, 0.6)',
        'dark': 'rgba(68, 68, 68, 0.6)'
      }
    }
  },
  'button': {
    'border': {
      'radius': '4px'
    }
  },
  'heading': {
    font: false
  }
});
var initialState = {
  themes: {
    grommet: __WEBPACK_IMPORTED_MODULE_6_grommet_themes_grommet__["grommet"],
    dark: __WEBPACK_IMPORTED_MODULE_5_grommet_themes_dark__["dark"],
    black: __WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes__["black"],
    materiallight: __WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes__["materiallight"],
    materialdark: __WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes__["materialdark"],
    metro: __WEBPACK_IMPORTED_MODULE_3_grommet_controls_themes__["metro"],
    custom: custom
  },
  selected: defaultTheme
};
/* harmony default export */ __webpack_exports__["a"] = (function () {
  var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : initialState;
  var action = arguments.length > 1 ? arguments[1] : undefined;

  switch (action.type) {
    case __WEBPACK_IMPORTED_MODULE_7__constants__["c" /* UPDATE_THEME */]:
      return _objectSpread({}, state, {
        themes: _objectSpread({}, state.themes, _defineProperty({}, action.name, action.theme)),
        selected: action.name
      });

    case __WEBPACK_IMPORTED_MODULE_7__constants__["a" /* DELETE_THEME */]:
      {
        var _this$state$themes = _this.state.themes,
            omit = _this$state$themes[action.name],
            rest = _objectWithoutProperties(_this$state$themes, [action.name]);

        return _objectSpread({}, state, {
          themes: rest
        });
      }

    case __WEBPACK_IMPORTED_MODULE_7__constants__["b" /* SELECT_THEME */]:
      return _objectSpread({}, state, {
        selected: state.themes[action.name] ? action.name : defaultTheme
      });

    default:
      return state;
  }
});

/***/ }),

/***/ 3:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__("./pages/index.js");


/***/ }),

/***/ "@babel/runtime/regenerator":
/***/ (function(module, exports) {

module.exports = require("@babel/runtime/regenerator");

/***/ }),

/***/ "grommet-controls/themes":
/***/ (function(module, exports) {

module.exports = require("grommet-controls/themes");

/***/ }),

/***/ "grommet-controls/themes/utils":
/***/ (function(module, exports) {

module.exports = require("grommet-controls/themes/utils");

/***/ }),

/***/ "grommet-icons/icons/Add":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/Add");

/***/ }),

/***/ "grommet-icons/icons/Connect":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/Connect");

/***/ }),

/***/ "grommet-icons/icons/Descend":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/Descend");

/***/ }),

/***/ "grommet-icons/icons/Grommet":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/Grommet");

/***/ }),

/***/ "grommet-icons/icons/LinkNext":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/LinkNext");

/***/ }),

/***/ "grommet-icons/icons/Menu":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/Menu");

/***/ }),

/***/ "grommet-icons/icons/System":
/***/ (function(module, exports) {

module.exports = require("grommet-icons/icons/System");

/***/ }),

/***/ "grommet/components/Accordion":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Accordion");

/***/ }),

/***/ "grommet/components/AccordionPanel":
/***/ (function(module, exports) {

module.exports = require("grommet/components/AccordionPanel");

/***/ }),

/***/ "grommet/components/Anchor":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Anchor");

/***/ }),

/***/ "grommet/components/Box":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Box");

/***/ }),

/***/ "grommet/components/Button":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Button");

/***/ }),

/***/ "grommet/components/Calendar":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Calendar");

/***/ }),

/***/ "grommet/components/Carousel":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Carousel");

/***/ }),

/***/ "grommet/components/Chart":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Chart");

/***/ }),

/***/ "grommet/components/CheckBox":
/***/ (function(module, exports) {

module.exports = require("grommet/components/CheckBox");

/***/ }),

/***/ "grommet/components/Clock":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Clock");

/***/ }),

/***/ "grommet/components/DataTable":
/***/ (function(module, exports) {

module.exports = require("grommet/components/DataTable");

/***/ }),

/***/ "grommet/components/Diagram":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Diagram");

/***/ }),

/***/ "grommet/components/Distribution":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Distribution");

/***/ }),

/***/ "grommet/components/DropButton":
/***/ (function(module, exports) {

module.exports = require("grommet/components/DropButton");

/***/ }),

/***/ "grommet/components/FormField":
/***/ (function(module, exports) {

module.exports = require("grommet/components/FormField");

/***/ }),

/***/ "grommet/components/Grommet":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Grommet");

/***/ }),

/***/ "grommet/components/Heading":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Heading");

/***/ }),

/***/ "grommet/components/Image":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Image");

/***/ }),

/***/ "grommet/components/Layer":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Layer");

/***/ }),

/***/ "grommet/components/Menu":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Menu");

/***/ }),

/***/ "grommet/components/Meter":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Meter");

/***/ }),

/***/ "grommet/components/Paragraph":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Paragraph");

/***/ }),

/***/ "grommet/components/RadioButton":
/***/ (function(module, exports) {

module.exports = require("grommet/components/RadioButton");

/***/ }),

/***/ "grommet/components/RangeInput":
/***/ (function(module, exports) {

module.exports = require("grommet/components/RangeInput");

/***/ }),

/***/ "grommet/components/RangeSelector":
/***/ (function(module, exports) {

module.exports = require("grommet/components/RangeSelector");

/***/ }),

/***/ "grommet/components/Select":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Select");

/***/ }),

/***/ "grommet/components/Stack":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Stack");

/***/ }),

/***/ "grommet/components/Table":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Table");

/***/ }),

/***/ "grommet/components/TableBody":
/***/ (function(module, exports) {

module.exports = require("grommet/components/TableBody");

/***/ }),

/***/ "grommet/components/TableCell":
/***/ (function(module, exports) {

module.exports = require("grommet/components/TableCell");

/***/ }),

/***/ "grommet/components/TableHeader":
/***/ (function(module, exports) {

module.exports = require("grommet/components/TableHeader");

/***/ }),

/***/ "grommet/components/TableRow":
/***/ (function(module, exports) {

module.exports = require("grommet/components/TableRow");

/***/ }),

/***/ "grommet/components/Text":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Text");

/***/ }),

/***/ "grommet/components/TextArea":
/***/ (function(module, exports) {

module.exports = require("grommet/components/TextArea");

/***/ }),

/***/ "grommet/components/TextInput":
/***/ (function(module, exports) {

module.exports = require("grommet/components/TextInput");

/***/ }),

/***/ "grommet/components/Video":
/***/ (function(module, exports) {

module.exports = require("grommet/components/Video");

/***/ }),

/***/ "grommet/components/WorldMap":
/***/ (function(module, exports) {

module.exports = require("grommet/components/WorldMap");

/***/ }),

/***/ "grommet/contexts/ResponsiveContext":
/***/ (function(module, exports) {

module.exports = require("grommet/contexts/ResponsiveContext");

/***/ }),

/***/ "grommet/contexts/ThemeContext":
/***/ (function(module, exports) {

module.exports = require("grommet/contexts/ThemeContext");

/***/ }),

/***/ "grommet/themes/dark":
/***/ (function(module, exports) {

module.exports = require("grommet/themes/dark");

/***/ }),

/***/ "grommet/themes/grommet":
/***/ (function(module, exports) {

module.exports = require("grommet/themes/grommet");

/***/ }),

/***/ "grommet/utils/colors":
/***/ (function(module, exports) {

module.exports = require("grommet/utils/colors");

/***/ }),

/***/ "grommet/utils/object":
/***/ (function(module, exports) {

module.exports = require("grommet/utils/object");

/***/ }),

/***/ "next/head":
/***/ (function(module, exports) {

module.exports = require("next/head");

/***/ }),

/***/ "next/link":
/***/ (function(module, exports) {

module.exports = require("next/link");

/***/ }),

/***/ "next/router":
/***/ (function(module, exports) {

module.exports = require("next/router");

/***/ }),

/***/ "polished":
/***/ (function(module, exports) {

module.exports = require("polished");

/***/ }),

/***/ "prop-types":
/***/ (function(module, exports) {

module.exports = require("prop-types");

/***/ }),

/***/ "react":
/***/ (function(module, exports) {

module.exports = require("react");

/***/ }),

/***/ "react-ga":
/***/ (function(module, exports) {

module.exports = require("react-ga");

/***/ }),

/***/ "react-redux":
/***/ (function(module, exports) {

module.exports = require("react-redux");

/***/ }),

/***/ "react-redux/lib/utils/PropTypes":
/***/ (function(module, exports) {

module.exports = require("react-redux/lib/utils/PropTypes");

/***/ }),

/***/ "recompose/getDisplayName":
/***/ (function(module, exports) {

module.exports = require("recompose/getDisplayName");

/***/ }),

/***/ "redux":
/***/ (function(module, exports) {

module.exports = require("redux");

/***/ }),

/***/ "redux-devtools-extension":
/***/ (function(module, exports) {

module.exports = require("redux-devtools-extension");

/***/ }),

/***/ "redux-thunk":
/***/ (function(module, exports) {

module.exports = require("redux-thunk");

/***/ }),

/***/ "styled-components":
/***/ (function(module, exports) {

module.exports = require("styled-components");

/***/ }),

/***/ "url-search-params":
/***/ (function(module, exports) {

module.exports = require("url-search-params");

/***/ })

/******/ });
//# sourceMappingURL=index.js.map