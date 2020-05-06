Math.slope = function(xVals,yVals) {
    if(!xVals){
        const mult=1;
        xVals = [];
        Object.keys(yVals).forEach(idx=> xVals.push((idx-0+1)*mult));
    }
    return getSlope(xVals, yVals);
}

export function findLineByLeastSquares(values_x, values_y) {
    var sum_x = 0;
    var sum_y = 0;
    var sum_xy = 0;
    var sum_xx = 0;
    var count = 0;

    /*
     * We'll use those variables for faster read/write access.
     */
    var x = 0;
    var y = 0;
    var values_length = values_x.length;

    if (values_length != values_y.length) {
        throw new Error('The parameters values_x and values_y need to have same size!');
    }

    /*
     * Nothing to do.
     */
    if (values_length === 0) {
        return [ [], [] ];
    }

    /*
     * Calculate the sum for each of the parts necessary.
     */
    for (var v = 0; v < values_length; v++) {
        x = values_x[v];
        y = values_y[v];
        sum_x += x-0;
        sum_y += y-0;
        sum_xx += x*x;
        sum_xy += x*y;
        count++;
    }

    /*
     * Calculate m and b for the formular:
     * y = x * m + b
     */
    var m = (count*sum_xy - sum_x*sum_y) / (count*sum_xx - sum_x*sum_x);
    var b = (sum_y/count) - (m*sum_x)/count;

    /*
     * We will make the x and y result line now
     */
    var result_values_x = [];
    var result_values_y = [];

    for (var v = 0; v < values_length; v++) {
        x = values_x[v];
        y = x * m + b;
        result_values_x.push(x);
        result_values_y.push(y);
    }

    return [result_values_x, result_values_y];
}

export function getSlope(values_x, values_y) {
    var sum_x = new Number(0);
    var sum_y = new Number(0);
    var sum_xy = 0;
    var sum_xx = 0;
    var count = 0;
    let mX = 0;   // Mean of x
    let mY = 0;  //Mean of y

    /*
     * We'll use those variables for faster read/write access.
     */
    var x = 0;
    var y = 0;
    var values_length = values_x.length;

    if (values_length != values_y.length) {
        throw new Error('The parameters values_x and values_y need to have same size!');
    }

    /*
     * Nothing to do.
     */
    if (values_length === 0) {
        return [ [], [] ];
    }

    // Calc the means


    /*
     * Calculate the sum for each of the parts necessary.
     */
    for (var v = 0; v < values_length; v++) {
        x = values_x[v];
        y = values_y[v];
        sum_x += x-0;
        sum_y += y-0;
        count++;
    }
    mX = sum_x/values_length;
    mY = sum_y/values_length;
    for(var v=0; v < values_length; v++){
        x = values_x[v];
        y = values_y[v];
        sum_xx += (x-mX)*(x-mX);
        sum_xy += (x-mX)*(y-mY);
    }

    /*
     * Calculate m and b for the formular:
     * y = x * m + b
     */
    var m = sum_xy/sum_xx;
    // var m = (count*sum_xy - sum_x*sum_y) / (count*sum_xx - sum_x*sum_x);
    var b = (sum_y/count) - (m*sum_x)/count;

    return m;

}

export function niceNumber(x) {
    if(x==null) return null;
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

