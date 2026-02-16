.PHONY: run-fpga
run-fpga: fpga
	./sharpen

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./sharpen.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./sharpen.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./sharpen.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./sharpen.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: sharpen.prj

sharpen.prj: sharpen.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl --clock 175MHz ./sharpen.cpp -o sharpen

.PHONY: run-cpu
run-cpu: cpu
	./sharpen-cpu

.PHONY: cpu
cpu: sharpen-cpu

sharpen-cpu: sharpen.cpp
	i++ -march='x86-64' ./sharpen.cpp -o sharpen-cpu

.PHONY: clean
clean:
	rm -rf sharpen.prj/ sharpen sharpen-cpu transcript
