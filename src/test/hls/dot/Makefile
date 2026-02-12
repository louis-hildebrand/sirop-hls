.PHONY: run-fpga
run-fpga: fpga
	./dot

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./dot.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./dot.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./dot.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./dot.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: dot.prj

dot.prj: dot.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl ./dot.cpp -o dot

.PHONY: run-cpu
run-cpu: cpu
	./dot-cpu

.PHONY: cpu
cpu: dot-cpu

dot-cpu: dot.cpp
	i++ -march='x86-64' ./dot.cpp -o dot-cpu

.PHONY: clean
clean:
	rm -rf dot.prj/ dot dot-cpu transcript
