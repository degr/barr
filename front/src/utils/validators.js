export const requiredField = value => {
    let requiredFieldErrorMsg = 'Field required';
    if (value) return undefined;
    return requiredFieldErrorMsg;
};

export const maxLengthCreator = (maxValue) => value => {
    let maxValueErrorMsg = 'input is too big ' + maxValue;
    if (value && value.length > maxValue) {
        return maxValueErrorMsg;
    }
    return undefined;
};
export const minLengthCreator = (minValue) => value => {
    let minValueErrorMsg = 'input string is too short ' + minValue;
    if (value && value.length < minValue) {
        return minValueErrorMsg;
    }
    return undefined;
};

export const passwordsMustMatch = (value, allValues) => {
    return value !== allValues.password ?
        "localizeText('pass_doesnt_match')" :
        undefined;
};