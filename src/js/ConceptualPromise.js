/**
 * @callback thenCallback
 * @param {*} resolvedValue the value to resolve to
 */

/**
 * @callback catchCallback
 * @param {*} reason the reason the for rejection
 */

/**
 * @callback executorCallback
 * @param {thenCallback} resolve - callback handler for a resolved value
 * @param {catchCallback} reject - callback handler for a rejected value
 */

/**
 * A simple implementation of {@link Promise}.
 *
 * Do not provide any concurrency support, but shows how the other aspects of a Promise work.
 */
class ConceptualPromise {

    /**
     * @param {executorCallback} executor - handler for when this receives a value
     */
    constructor(executor) {
        // no concurrency, but support the Promise "flow"
        executor(
            (resolveValue) => {
                this.resolvedValue = resolveValue
            },
            (rejectedValue) => {
                this.rejectedValue = rejectedValue
            }
        );
    }

    /**
     * If this contains a value, pass it to the given handler.
     *
     * @param {thenCallback} callback - handler for the passed value
     * @returns {ConceptualPromise}
     */
    then(callback) {
        if (this.rejectedValue !== undefined) {
            return ConceptualPromise.rejected(this.rejectedValue)
        } else {
            return new ConceptualPromise((resolve, reject) => {
                try {
                    resolve(callback(this.resolvedValue))
                } catch (e) {
                    reject(e)
                }
            })
        }
    }

    /**
     * If this contains a rejection, invokes the provided callback.
     *
     * @param {catchCallback} callback - error handler
     * @returns {ConceptualPromise}
     */
    catch(callback) {
        if (this.rejectedValue !== undefined) {
            const reason = callback(this.rejectedValue) || this.rejectedValue;
            return ConceptualPromise.rejected(reason);
        } else {
            return ConceptualPromise.resolved(this.resolvedValue);
        }
    }

    // *********************************
    //
    // STATIC METHODS
    //
    // *********************************

    /**
     * Create a ConceptualPromise that contains the given value.
     *
     * @param {*} resolvedValue - the value to resolve to
     * @returns {ConceptualPromise}
     */
    static resolved(resolvedValue) {
        return (resolvedValue instanceof ConceptualPromise) ?
            resolvedValue :
            new ConceptualPromise((resolve, reject) => {
                resolve(resolvedValue)
            });
    }

    /**
     * Create a ConceptualPromise that is in a rejected state.
     *
     * @param {*} reason - the reason the for rejection
     * @returns {ConceptualPromise}
     */
    static rejected(reason) {
        return (reason instanceof ConceptualPromise) ?
            reason :
            new ConceptualPromise((resolve, reject) => {
                reject(reason)
            });
    }
}

// ********************************************
// ********************************************

// Demonstrate usage
// When this is run, the following is logged to the console:
//
//    then: resolved value and more
//    caught YIKES
//

ConceptualPromise.resolved("resolved value")
    .then(theVal => {
        return theVal + " and more"
    })
    .then(theVal => {
        console.log("then: " + theVal)
    })
    .then(theVal => {
        // trigger a rejection
        throw "YIKES";
    })
    .then(theVal => {
        console.log("This should not be invoked because there has been a rejection")
    })
    .catch(e => {
        console.log("caught " + e)
    })
    .then(theVal => {
        console.log("This should not be invoked because there has been a rejection")
    });
