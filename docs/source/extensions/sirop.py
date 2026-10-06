"""
Sphinx extension for showing Sirop function signatures properly.
"""

# pylint: disable=fixme

# This code is based on the tutorial at
# https://www.sphinx-doc.org/en/master/development/tutorials/adding_domain.html
# TODO: Also add an index, as shown in that tutorial?

import typing

from sphinx import addnodes
from sphinx.application import Sphinx
from sphinx.directives import ObjectDescription
from sphinx.domains import Domain
from sphinx.roles import XRefRole
from sphinx.util.nodes import make_refnode
from sphinx.util.typing import ExtensionMetadata

import sirop_parser as parse


class SiropFunctionDirective(ObjectDescription):
    """A custom directive that describes a Sirop function."""

    has_content = True
    required_arguments = 1

    def handle_signature(self, sig, signode):
        (name, params, typ, comment) = parse.signature(sig)
        signode += addnodes.desc_name(text=name)
        signode += addnodes.desc_parameterlist(
            text=", ".join(p for p in params)
        )
        signode += addnodes.desc_sig_element(text=":")
        signode += addnodes.desc_sig_space()
        signode += addnodes.desc_type(text=typ)
        # TODO: slightly grey it out, rather than italicizing it?
        if comment.strip():
            signode += addnodes.literal_emphasis(text=f", where {comment}")
        return name

    def add_target_and_index(self, name, sig, signode):
        signode['ids'].append(name)
        domain = typing.cast(SiropDomain, self.env.get_domain('sirop'))
        domain.add_function(name)


# pylint: disable-next=abstract-method
class SiropDomain(Domain):
    """The custom 'sirop' domain."""

    name = 'sirop'
    label = 'Sirop'
    roles = {
        'func': XRefRole(),
    }
    directives = {
        'function': SiropFunctionDirective,
    }
    initial_data = {
        'functions': [],
    }
    data_version = 0

    def fully_qualify(self, name: str) -> str:
        domain_name = self.name
        return f'{domain_name}.{name}'

    def get_objects(self):
        yield from self.data['functions']

    def resolve_xref(self, env, fromdocname, builder, typ, target, node, contnode):
        matches = [
            (docname, anchor)
            for full_name, name, typ, docname, anchor, prio in self.get_objects()
            if name == target
        ]
        if matches:
            (todocname, targ) = matches[0]
            return make_refnode(builder, fromdocname, todocname, targ, contnode, targ)
        return None

    def add_function(self, name):
        """Add a new function to the domain."""
        fully_qualified_name = self.fully_qualify(name)
        typ = 'Sirop Function'
        docname = self.env.current_document.docname
        anchor = name
        priority = 0
        obj = (fully_qualified_name, name, typ, docname, anchor, priority)
        self.data['functions'].append(obj)


def setup(app: Sphinx) -> ExtensionMetadata:
    app.add_domain(SiropDomain)
    return {
        'version': '0.1',
        'parallel_read_safe': True,
        'parallel_write_safe': True,
    }
