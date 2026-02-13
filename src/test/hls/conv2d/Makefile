.PHONY: run-fpga
run-fpga: fpga
	./conv2d

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./conv2d.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./conv2d.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./conv2d.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./conv2d.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: conv2d.prj

conv2d.prj: conv2d.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl --clock 175MHz ./conv2d.cpp -o conv2d

.PHONY: run-cpu
run-cpu: cpu
	./conv2d-cpu

.PHONY: cpu
cpu: conv2d-cpu

conv2d-cpu: conv2d.cpp
	i++ -march='x86-64' ./conv2d.cpp -o conv2d-cpu

.PHONY: clean
clean:
	rm -rf conv2d.prj/ conv2d conv2d-cpu transcript
