package org.dflib.exp.sort;

import org.dflib.Exp;


/**
 * @deprecated moved to {@link org.dflib.sort.ExpSorter}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class ExpSorter extends org.dflib.sort.ExpSorter {

    public ExpSorter(Exp<?> exp, boolean ascending) {
        super(exp, ascending);
    }
}
