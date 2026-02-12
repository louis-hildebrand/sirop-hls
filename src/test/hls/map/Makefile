.PHONY: run-fpga
run-fpga: fpga
	./map

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./map.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./map.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./map.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./map.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: map.prj

map.prj: map.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl ./map.cpp -o map

.PHONY: run-cpu
run-cpu: cpu
	./map-cpu

.PHONY: cpu
cpu: map-cpu

map-cpu: map.cpp
	i++ -march='x86-64' ./map.cpp -o map-cpu

.PHONY: clean
clean:
	rm -rf map.prj/ map map-cpu transcript
