'use strict';

// CurrencyFormatter.js
// ---------------------------------------------------------------------
// Version: 2.0
// Release Date: 9 Jan 2018
// Created by the Bx team at OSREC Technologies (https://osrec.co.uk)
//
// Check out Bx @ https://usebx.com for free invoicing, expenses & project management
// If you use this library in a commercial project, we appreciate a link back to https://osrec.co.uk :)

var OSREC = OSREC || {};

OSREC.CurrencyFormatter = {

	defaultLocales: {
		INR: 'en_IN'

	},

	locales: {
		en_IN: { p: '! #,##,##0.00', g: ',', d: '.' }
	},

	getFormatDetails: function getFormatDetails(p) {
		var locales = OSREC.CurrencyFormatter.locales;
		var defaultLocales = OSREC.CurrencyFormatter.defaultLocales;

		var locale, currency, pattern, decimal, group, valueOnError;

		// Perform checks on inputs and set up defaults as needed (defaults to en, USD)

		p = p || {};

		currency = (p.currency || 'INR').toUpperCase();
		locale = locales[p.locale || defaultLocales[currency]];

		if (typeof locale.h !== 'undefined') {
			locale = locales[locale.h];
		} // Locale inheritance

		pattern = p.pattern || locale.p;
		decimal = p.decimal || locale.d;
		group = p.group || locale.g;
		valueOnError = typeof p.valueOnError === 'undefined' ? 0 : p.valueOnError;

		var formatDetails = {
			pattern: pattern,
			decimal: decimal,
			group: group,
			valueOnError: valueOnError,
			postFormatFunction: p.postFormatFunction
		};

		return formatDetails;
	},

	toFixed: function toFixed(n, precision) {
		return (Math.round(Number(n) * Math.pow(10, precision)) / Math.pow(10, precision)).toFixed(precision);
	},

	getFormatter: function getFormatter(p) {
		var formatDetails = OSREC.CurrencyFormatter.getFormatDetails(p);

		var pattern = formatDetails.pattern;
		var decimal = formatDetails.decimal;
		var group = formatDetails.group;
		var valueOnError = formatDetails.valueOnError;
		var postFormatFunction = formatDetails.postFormatFunction;

		// encodePattern Function - returns a few simple characteristics of the pattern provided

		var encodePattern = function encodePattern(pattern) {
			var numberFormatPattern = pattern.trim().match(/[#0,\.]+/)[0];

			var split = numberFormatPattern.split('.');
			var c = split[0]; // Decimal chars
			var m = split[1]; // Decimal mantissa

			var groups = c.split(',');
			var groupLengths = groups.map(function (g) {
				return g.length;
			});
			var zeroLength = (groups[groups.length - 1].match(/0/g) || []).length;
			var decimalPlaces = typeof m === 'undefined' ? 0 : m.length;
			var paddingSplit = pattern.split(numberFormatPattern);

			var encodedPattern = {
				pattern: pattern,
				decimalPlaces: decimalPlaces,
				frontPadding: paddingSplit[0],
				backPadding: paddingSplit[1],
				groupLengths: groupLengths,
				zeroLength: zeroLength
			};

			return encodedPattern;
		};

		// Zero Padding helper function

		var pad = function pad(n, width) {
			n = n + '';
			return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;
		};

		// Format function

		var format = function format(n, f) {
			var formattedNumber = OSREC.CurrencyFormatter.toFixed(Math.abs(n), f.decimalPlaces);

			var splitNumber = formattedNumber.split(".");

			var segment = "";

			var cursor = splitNumber[0].length;

			var maxGroupIndex = f.groupLengths.length - 1;

			var groupIndex = maxGroupIndex;

			if (maxGroupIndex > 0) {
				while (cursor > 0) {
					if (groupIndex < 1) {
						groupIndex = 1;
					} // Always reset to the last group length (useful for big numbers)

					var currentGroupLength = f.groupLengths[groupIndex];

					var start = cursor - currentGroupLength;

					segment = splitNumber[0].substring(start, cursor) + f.group + segment;

					cursor -= currentGroupLength;

					--groupIndex;
				}

				segment = segment.substring(0, segment.length - 1);
			} else {
				segment = splitNumber[0];
			}

			if (segment.length < f.zeroLength) {
				segment = pad(segment, f.zeroLength);
			}

			var formattedNumber = f.frontPadding + segment + (typeof splitNumber[1] === 'undefined' ? '' : f.decimal + splitNumber[1]) + f.backPadding;

			return formattedNumber.replace(/\!/g, ' ').trim();
		};

		// Use encode function to work out pattern

		var patternArray = pattern.split(";");

		var positiveFormat = encodePattern(patternArray[0]);

		positiveFormat.decimal = decimal;
		positiveFormat.group = group;

		var negativeFormat = typeof patternArray[1] === 'undefined' ? encodePattern("-" + patternArray[0]) : encodePattern(patternArray[1]);

		negativeFormat.decimal = decimal;
		negativeFormat.group = group;

		var zero = typeof patternArray[2] === 'undefined' ? format(0, positiveFormat) : patternArray[2];

		return function (n) {
			if (isNaN(n)) {
				return valueOnError;
			}
			var formattedNumber;
			n = Number(n);
			if (n > 0) {
				formattedNumber = format(n, positiveFormat);
			} else if (n == 0) {
				formattedNumber = zero.replace('!', ' ');
			} else {
				formattedNumber = format(n, negativeFormat);
			}
			return typeof postFormatFunction === 'function' ? postFormatFunction(n, formattedNumber) : formattedNumber;
		};
	},

	formatAll: function formatAll(p) {
		var formatter = OSREC.CurrencyFormatter.getFormatter(p);

		var matches = document.querySelectorAll(p.selector);

		for (var i = 0; i < matches.length; ++i) {
			matches[i].innerHTML = formatter(matches[i].textContent);
		}
	},

	formatEach: function formatEach(selector) {
		var formatters = {};

		var matches = document.querySelectorAll(selector);

		for (var i = 0; i < matches.length; ++i) {
			try {

				var ccy = matches[i].getAttribute("data-ccy");

				if (typeof formatters[ccy] === 'undefined') {
					formatters[ccy] = OSREC.CurrencyFormatter.getFormatter({ currency: ccy });
				}

				var formatter = formatters[ccy];

				matches[i].innerHTML = formatter(matches[i].textContent);
			} catch (e) {
				console.log(e);
			}
		}
	},

	format: function format(n, p) {
		var formatterFunction = OSREC.CurrencyFormatter.getFormatter(p);

		return formatterFunction(n);
	},

	parse: function parse(str, p) {
		var decimal = OSREC.CurrencyFormatter.getFormatDetails(p).decimal;
		var mult = str.indexOf('-') >= 0 ? -1 : 1;
		return Math.abs(Number(str.replace(new RegExp('[^0-9' + decimal + ']', 'g'), '').replace(decimal, '.'))) * mult;
	}
};

var hasDefine = typeof define === 'function';
var hasExports = typeof module !== 'undefined' && module.exports;
var root = typeof window === 'undefined' ? global : window;

if (hasDefine) {
	// AMD Module
	define([], function () {
		return OSREC.CurrencyFormatter;
	});
} else if (hasExports) {
	// Node.js Module
	module.exports = OSREC.CurrencyFormatter;
} else {
	// Assign to the global object
	// This makes sure that the object really is assigned to the global scope
	root.OSREC = OSREC;
}