.PHONY: run-fpga
run-fpga: fpga
	./conv1d

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./conv1d.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./conv1d.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./conv1d.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./conv1d.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: conv1d.prj

conv1d.prj: conv1d.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl ./conv1d.cpp -o conv1d

.PHONY: run-cpu
run-cpu: cpu
	./conv1d-cpu

.PHONY: cpu
cpu: conv1d-cpu

conv1d-cpu: conv1d.cpp
	i++ -march='x86-64' ./conv1d.cpp -o conv1d-cpu

.PHONY: clean
clean:
	rm -rf conv1d.prj/ conv1d conv1d-cpu
