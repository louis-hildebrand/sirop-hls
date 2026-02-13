.PHONY: run-fpga
run-fpga: fpga
	./convb2b

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./convb2b.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./convb2b.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./convb2b.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./convb2b.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: convb2b.prj

convb2b.prj: convb2b.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl ./convb2b.cpp -o convb2b

.PHONY: run-cpu
run-cpu: cpu
	./convb2b-cpu

.PHONY: cpu
cpu: convb2b-cpu

convb2b-cpu: convb2b.cpp
	i++ -march='x86-64' ./convb2b.cpp -o convb2b-cpu

.PHONY: clean
clean:
	rm -rf convb2b.prj/ convb2b convb2b-cpu transcript
