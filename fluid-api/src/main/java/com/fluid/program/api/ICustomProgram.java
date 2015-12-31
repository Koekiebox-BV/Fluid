package com.fluid.program.api;

import java.util.List;

import com.fluid.program.api.vo.FluidItem;

/**
 * 
 */
public interface ICustomProgram extends IActionBase {

    /**
     * 
     * @return
     */
    public abstract String getTaskIdentifier();

    /**
     * 
     * @param fluidItemParam
     * @return
     * @throws Exception
     */
    public abstract List<FluidItem> execute(FluidItem fluidItemParam) throws Exception;

    /**
     *
     * 
     * @param fluidItemParam
     * @return
     * @throws Exception
     */
    public abstract String executePerFluidItemQuery(FluidItem fluidItemParam) throws Exception;

    /**
     * @param fluidItemParam - Each of the original items from <code>execute(FluidItem fluidItemParam)</code>. There is an option to set
     *                       a parent for the Fluid item that most likely would originate from the <code>executePerFluidItemQueryResultParam</code>.
     * @param executePerFluidItemQueryResultParam - The <code>executePerFluidItemQuery(FluidItem fluidItemParam)</code> result. Only
     *                                            attachments can be added.
     * @return
     * @throws Exception
     */
    public abstract FluidItem postProcessFluidItem(FluidItem fluidItemParam,
                                                   List<FluidItem> executePerFluidItemQueryResultParam) throws Exception;
}
