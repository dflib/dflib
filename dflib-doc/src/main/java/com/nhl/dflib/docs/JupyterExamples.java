package com.nhl.dflib.docs;

import com.nhl.dflib.jupyter.DFLibJupyter;

public class JupyterExamples {

    public void setDisplayParams() {

        // tag::setDisplayParams[]
        DFLibJupyter.setMaxDisplayRows(10);
        DFLibJupyter.setMaxDisplayColumnWidth(50);
        // end::setDisplayParams[]
    }
}
