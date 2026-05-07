`timescale 1ns/1ps

module Test;

    reg clock, reset, valid_up;
    wire valid_down;
    reg [7:0] I;
    wire [7:0] O;

    Top DUT (
        .clock(clock),
        .reset(reset),
        .valid_up(valid_up),
        .valid_down(valid_down),
        .I(I),
        .O(O)
    );

    task wait_for_output ();
    begin
        wait(clock == 1 && valid_down);
        wait(clock == 0);
    end
    endtask

    task check_output (input [7:0] expected, input [7:0] actual);
    begin
        $display("OUTPUT : %h", actual);
        if (actual != expected) begin
            $error("ASSERTION FAILED: expected %h, got %h", expected, actual);
        end
    end
    endtask

    task initialize ();
    begin
        $display("Initializing design...");
        reset = 1;
        @(posedge clock) begin
            reset = 0;
        end
    end
    endtask

    initial begin
        $display("Started clock generator.");
        clock = 0;
        forever
            #5 clock = ~clock;
    end

    initial begin
        $display("Started test stimuli generator.");
        valid_up = 0;
        initialize();

        @(posedge clock) begin
            I = 8'd42;
            valid_up = 1;
        end
        @(posedge clock) I = 8'd43;
        @(posedge clock) I = 8'd44;
        @(posedge clock) I = 8'd45;
        @(posedge clock) I = 8'd46;
    end

    initial begin
        $display("Started output checker.");

        wait_for_output(); check_output(8'd47, O);
        wait_for_output(); check_output(8'd48, O);
        wait_for_output(); check_output(8'd49, O);
        wait_for_output(); check_output(8'd50, O);
        wait_for_output(); check_output(8'd51, O);

        $display("Simulation done.");
        $stop(0);
    end

endmodule
