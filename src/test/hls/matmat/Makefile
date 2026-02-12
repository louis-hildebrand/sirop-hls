.PHONY: run-fpga
run-fpga: fpga
	./matmat

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./matmat.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./matmat.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./matmat.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./matmat.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: matmat.prj

matmat.prj: matmat.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl ./matmat.cpp -o matmat

.PHONY: run-cpu
run-cpu: cpu
	./matmat-cpu

.PHONY: cpu
cpu: matmat-cpu

matmat-cpu: matmat.cpp
	i++ -march='x86-64' ./matmat.cpp -o matmat-cpu

.PHONY: clean
clean:
	rm -rf matmat.prj/ matmat matmat-cpu transcript
