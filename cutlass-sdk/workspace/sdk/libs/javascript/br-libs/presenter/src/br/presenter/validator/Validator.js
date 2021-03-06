var Errors = require('br/Errors');

/**
 * This is an interface and should not be constructed.
 * @class
 * @interface
 * A validator is a piece of code that can determine whether particular input should be considered valid.
 * 
 * Since you may want to run many validators on a piece of data, the result of the validation is stored on an
 * object passed into the validate method.
 */
br.presenter.validator.Validator = function()
{
};

/**
 * Determine whether the provided value is valid or not and set the result on the provided {@link br.presenter.validator.ValidationResult}.
 * 
 * @param {Object} vValue The value to validate.
 * @param {Object} mAttributes attributes to control the validation process. Will not be null.
 * @param {br.presenter.validator.ValidationResult} oValidationResult the ValidationResult to store the result of this validation in.
 */
br.presenter.validator.Validator.prototype.validate = function(vValue, mAttributes, oValidationResult)
{
	throw new Errors.UnimplementedInterfaceError("Validator.validate() has not been implemented.");
};
